
package com.example.campusfind_teamarslan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusFind.db";
    private static final int DATABASE_VERSION = 3;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create database tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable =
                "CREATE TABLE users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "full_name TEXT NOT NULL, " +
                        "email TEXT NOT NULL COLLATE NOCASE UNIQUE, " +
                        "password_hash TEXT NOT NULL, " +
                        "password_salt TEXT NOT NULL" +
                        ")";

        db.execSQL(createUsersTable);

        createItemsTable(db);

        // Insert six sample items
        seedStarterItems(db);
    }

    // Create items table
    private void createItemsTable(SQLiteDatabase db) {

        String createItemsTable =
                "CREATE TABLE IF NOT EXISTS items (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "item_code TEXT, " +
                        "user_email TEXT NOT NULL, " +
                        "reported_by TEXT, " +
                        "item_name TEXT NOT NULL, " +
                        "category TEXT NOT NULL, " +
                        "description TEXT, " +
                        "status TEXT NOT NULL, " +
                        "return_status TEXT DEFAULT 'Open', " +
                        "contact_number TEXT, " +
                        "location TEXT NOT NULL, " +
                        "date_reported TEXT NOT NULL, " +
                        "latitude REAL, " +
                        "longitude REAL, " +
                        "image_uri TEXT, " +
                        "is_returned INTEGER DEFAULT 0" +
                        ")";

        db.execSQL(createItemsTable);
    }

    private void seedStarterItems(SQLiteDatabase db) {

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM items WHERE item_code IS NOT NULL",
                null
        );

        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count > 0) {
            return;
        }

        addStarterItem(db, "CF-01", "FOUND",
                "Black leather wallet", "Wallets & Cards",
                "Main Library", "2026-11-12", 31.38955, 74.24070);

        addStarterItem(db, "CF-02", "LOST",
                "Casio fx-991ES calculator", "Electronics",
                "C5 Block", "2026-11-11", 31.38990, 74.24120);

        addStarterItem(db, "CF-03", "FOUND",
                "Student ID card", "Wallets & Cards",
                "Cafeteria", "2026-11-10", 31.38880, 74.24010);

        addStarterItem(db, "CF-04", "LOST",
                "Blue water bottle", "Other",
                "Sports Ground", "2026-11-09", 31.38860, 74.24200);

        addStarterItem(db, "CF-05", "FOUND",
                "Car keys, red keychain", "Keys",
                "Main Parking", "2026-11-08", 31.39050, 74.23950);

        addStarterItem(db, "CF-06", "LOST",
                "32 GB USB flash drive", "Electronics",
                "Mosque", "2026-11-07", 31.38920, 74.24180);
    }

    private void addStarterItem(
            SQLiteDatabase db,
            String code,
            String status,
            String name,
            String category,
            String location,
            String date,
            double latitude,
            double longitude) {

        ContentValues values = new ContentValues();

        values.put("item_code", code);
        values.put("user_email", "studentaffairs@campusfind.local");
        values.put("reported_by", "Student Affairs");
        values.put("item_name", name);
        values.put("category", category);
        values.put("description", "Starter sample item");
        values.put("status", status);
        values.put("return_status", "Open");
        values.put("location", location);
        values.put("date_reported", date);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("is_returned", 0);

        db.insert("items", null, values);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {

        if (oldVersion < 2) {
            createItemsTable(db);
        }

        if (oldVersion >= 2 && oldVersion < 3) {

            db.execSQL("ALTER TABLE items ADD COLUMN item_code TEXT");
            db.execSQL("ALTER TABLE items ADD COLUMN reported_by TEXT");
            db.execSQL("ALTER TABLE items ADD COLUMN return_status TEXT DEFAULT 'Open'");
            db.execSQL("ALTER TABLE items ADD COLUMN contact_number TEXT");
        }

        seedStarterItems(db);
    }

    // Generate password hash
    private String generateHash(String password, String salt) {

        try {
            byte[] saltBytes = Base64.decode(salt, Base64.DEFAULT);

            KeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    saltBytes,
                    120000,
                    256
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.encodeToString(hash, Base64.DEFAULT);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Generate random salt
    private String generateSalt() {

        byte[] salt = new byte[16];

        new SecureRandom().nextBytes(salt);

        return Base64.encodeToString(salt, Base64.DEFAULT);
    }

    // Register new user
    public boolean registerUser(String name, String email,
                                String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        String salt = generateSalt();
        String passwordHash = generateHash(password, salt);

        ContentValues values = new ContentValues();

        values.put("full_name", name);
        values.put("email", email);
        values.put("password_hash", passwordHash);
        values.put("password_salt", salt);

        long result = db.insert("users", null, values);

        return result != -1;
    }

    // Check login credentials
    public boolean checkUser(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"password_hash", "password_salt"},
                "email = ?",
                new String[]{email},
                null,
                null,
                null
        );

        boolean isValid = false;

        if (cursor.moveToFirst()) {

            String savedHash = cursor.getString(0);
            String savedSalt = cursor.getString(1);

            String enteredHash = generateHash(password, savedSalt);

            isValid = MessageDigest.isEqual(
                    savedHash.getBytes(),
                    enteredHash.getBytes()
            );
        }

        cursor.close();

        return isValid;
    }
}
