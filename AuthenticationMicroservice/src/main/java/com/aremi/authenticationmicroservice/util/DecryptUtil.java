package com.aremi.authenticationmicroservice.util;

import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Classe di utilità per la Crittografia dei dati
 */

@Slf4j
public class DecryptUtil {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY = "Esu7XyiFiMy/hqGRRPL+ZXAf";

    /**
     * Cripta una stringa con Algoritmo AES-256 (con padding) e ritorna stringa in Base64
     * @param data Dato da cifrare
     * @return Stringa in Base64 cifrata
     * @throws Throwable Eccezioni di cifratura
     */
    public static String encrypt(String data) throws Throwable {
        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Genera una chiave segreta
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Converte i dati cifrati in stringa codificata in Base64
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * Decripta una stringa in Base64 cifrata con Algoritmo AES-256 (con padding)
     * @param encryptedData Dato in Base64 cifrato.
     * @return Stringa decriptata
     * @throws Throwable Eccezioni di cifratura
     */
    public static String decrypt(String encryptedData) throws Throwable {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }

    /**
     * Hasha una password in chiaro per salvataggio in DB, usa algoritmo di hashing BCrypt con sale
     * @param password Dato da cifrare
     * @return Hash risultante dalla cifratura
     */
    public static String hashPassword(String password) {
        // Genera un salt e hash della password (salt: valore casuale aggiunto alla password)
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Metodo che verifica se una determinata password (in chiaro) corrisponde a quella hashata salvata in database
     * @param password Password in chiaro da verificare
     * @param hashedPassword Password salvata in database, associata all'utenza
     * @return
     */
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public static void main(String[] args) {
        try {
            String username = "aremi";
            String password = "admin";

            String encryptedUsername = encrypt(username);
            String encryptedPassword = encrypt(password);
            log.info("enc_username:{}", encryptedUsername);
            log.info("enc_password:{}", encryptedPassword);

            String decryptedUsername = decrypt(encryptedUsername);
            String decryptedPassword = decrypt(encryptedPassword);
            log.info("dec_username:{}", decryptedUsername);
            log.info("dec_password:{}", decryptedPassword);

            String hashedPassword = hashPassword(password);
            log.info("hashed_password:{}", hashedPassword);

            boolean isPasswordCorrect = checkPassword(password, hashedPassword);
            log.info("isPasswordCorrect:{}", isPasswordCorrect);
        } catch (Throwable e) {
            log.error("DecryptUtil.class::main [ERROR] ", e);
        }
    }
}
