package com.ejercicio2.appinventario.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventario.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE productos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, stock INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }

    public int getStockByProductName(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT stock FROM productos WHERE nombre = ?", new String[]{nombre});
        if (cursor.moveToFirst()) {
            int stock = cursor.getInt(0);
            cursor.close();
            return stock;
        } else {
            cursor.close();
            return 0; // Si no se encuentra el producto, devolver 0
        }
    }

    // Otras operaciones CRUD para la base de datos
}

