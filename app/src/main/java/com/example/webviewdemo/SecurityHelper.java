package com.example.webviewdemo;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.io.FileUtils.readFileToByteArray;

/**
 * Created by kaushik on 5/10/18.
 */

public class SecurityHelper {

    private static final String TAG = SecurityHelper.class.getName();

    private static SecretKey yourKey = null;
    private static String algorithm = "AES/CFB/NoPadding";
    private String encryptedFileName = "encrypted_file";
    static String filePath;
    static Context mContext;

    public SecurityHelper(Context context) {
        mContext = context;
    }

    public SecurityHelper() {}

    public SecurityHelper(String path) {
        filePath = path;
        filePath += "/" + ".SVE/" ;
    }

    /*
    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }


    */

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        Log.i(TAG,"generating key");
        final int outputKeyLength = 256;
        SecureRandom secureRandom = new SecureRandom(new String("yourKey").getBytes());
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        yourKey = keyGenerator.generateKey();
        return yourKey;
    }

    private static byte[] decodeFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    /*
    public static void saveKey(SecretKey secretKey) {
        File file = new File(filePath,"file.key");
        char[] hex = Hex.encodeHex(secretKey.getEncoded());
        try {
            FileUtils.writeStringToFile(file, String.valueOf(hex), Charset.forName("UTF-8"));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    private static void loadKey() throws IOException {
        Log.i(TAG,"loadKey called");

        //File file = new File(Constants.DBX_BASE_PATH + Constants.DBX_CLOUD_PATH,"file.key");
     //   File keyFile = new File(mContext.getFilesDir().getAbsolutePath() + Constants.DBX_KEY_PATH,
       //         Constants.DBX_KEY_FILE);
        File keyFile=new File(mContext.getExternalFilesDir("").getAbsoluteFile().getAbsolutePath()+"/file.key");
        if(keyFile.exists()) {
           Log.i(TAG,"key file exists");
        }

        String data = new String(readFileToByteArray(keyFile));
        byte[] decoded=null;
        try {
            decoded = decodeHex(data.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        yourKey = new SecretKeySpec(decoded, "AES");
    }

    public boolean keyFileExists() {
        if(new File(mContext.getExternalFilesDir("").getAbsoluteFile().getAbsolutePath() + "/" + "file.key").exists())
            return true;
        else
            return false;
    }

    /*
    void saveFile(byte[] stringToSave) {
        try {

            //File file = new File(Environment.getExternalStorageDirectory() + File.separator, encryptedFileName);
            File file = new File(filePath,encryptedFileName);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            yourKey = generateKey();
            byte[] filesBytes = encodeFile(yourKey, stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

    public static byte[] decodeFile(String strFilePath) {
        byte[] decodedData = null;
        try {
            /*
            if(keyFileExists()) {
                Log.i(TAG,"loading key");
                yourKey = loadKey();
            }
            */
            /*
            else {
                Log.i(TAG,"generating key");
                yourKey = generateKey();
            }
            */
            if(yourKey==null) {
                //Log.i(TAG,"requires key generation");
                loadKey();
                //yourKey = generateKey();
            }
            //Log.i(TAG,"decoding data");
            decodedData = decodeFile(yourKey, readFile(strFilePath));
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
            //playMp3(decodedData);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return decodedData;
    }

    private static byte[] readFile(String strFilePath) {

        /*
        byte[] contents = null;
        File file = new File(filePath,encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        */

        byte[] contents = null;
        try {
            File temp = new File(strFilePath);
            InputStream inputStream = new FileInputStream(temp);
            contents = IOUtils.toByteArray(inputStream);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return contents;
    }

    /*
    public byte[] getAudioFile() throws FileNotFoundException
    {
        byte[] audio_data = null;
        byte[] inarry = null;
        //AssetManager am = getAssets();

        //filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //filePath += "/" + ".SVE/" ;

        try {
            //InputStream is = am.open("Sleep Away.mp3"); // use recorded file instead of getting file from assets folder.
            InputStream is = new FileInputStream(filePath+"IGoToSchoolOnFoot.mp3");
            int length = is.available();
            audio_data = new byte[length];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = is.read(audio_data)) != -1) {
                output.write(audio_data, 0, bytesRead);
            }
            inarry = output.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inarry;
    }
    */

    /*
    private void playMp3(byte[] mp3SoundByteArray) {

        try {
            // create temp file that will hold byte array
            final File tempMp3 = File.createTempFile(encryptedFileName, ".tmp", new File(filePath));
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();
            // Tried reusing instance of media player
            // but that resulted in system crashes...
            Log.i("SecurityHelper","playing media for file: "+tempMp3.getAbsolutePath());
            MediaPlayer mediaPlayer = new MediaPlayer();
            final FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {
                        fis.close();
                        tempMp3.delete();
                        mediaPlayer.release();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace();

        }

    }
    */
}
