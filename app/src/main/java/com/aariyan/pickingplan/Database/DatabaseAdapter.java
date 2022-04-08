package com.aariyan.pickingplan.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Model.PlanModel;
import com.aariyan.pickingplan.Model.RefModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {


    DatabaseHelper helper;
    private List<PlanModel> planList = new ArrayList<>();
    private List<RefModel> refList = new ArrayList<>();


    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);
    }

    //Insert Header:
    public long insertPlans(int intAutoPicking, String Storename, String Quantity, String ItemCode, String Description, String SalesOrderNo,
                            int OrderId, String mass, int LineNos, String weights, String OrderDate, String Instruction, String Area, String Toinvoice,
                            String toLoad, String reference) {

        SQLiteDatabase database = helper.getWritableDatabase();
//        database.execSQL(DatabaseHelper.DROP_PLANS_TABLE);
//        database.execSQL(DatabaseHelper.CREATE_PLANS_TABLE);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.intAutoPicking, intAutoPicking);
        contentValues.put(DatabaseHelper.Storename, Storename);
        contentValues.put(DatabaseHelper.Quantity, Quantity);
        contentValues.put(DatabaseHelper.ItemCode, ItemCode);
        contentValues.put(DatabaseHelper.Description, Description);
        contentValues.put(DatabaseHelper.SalesOrderNo, SalesOrderNo);
        contentValues.put(DatabaseHelper.OrderId, OrderId);
        contentValues.put(DatabaseHelper.mass, mass);
        contentValues.put(DatabaseHelper.LineNos, LineNos);
        contentValues.put(DatabaseHelper.weights, weights);
        contentValues.put(DatabaseHelper.OrderDate, OrderDate);
        contentValues.put(DatabaseHelper.Instruction, Instruction);
        contentValues.put(DatabaseHelper.Area, Area);
        contentValues.put(DatabaseHelper.Toinvoice, Toinvoice);
        contentValues.put(DatabaseHelper.toLoad, toLoad);
        contentValues.put(DatabaseHelper.reference, reference);
        contentValues.put(DatabaseHelper.FLAG, 1);
        contentValues.put(DatabaseHelper.IP, Constant.BASE_URL);

        long id = database.insert(DatabaseHelper.PLAN_TABLE_NAME, null, contentValues);
        return id;
    }

    //Insert Ref:
    public long insertRef(int intAutoPickingHeader,String strUnickReference,String strPickingNickname, int userID) {

        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.intAutoPickingHeader, intAutoPickingHeader);
        contentValues.put(DatabaseHelper.userId, userID);
        contentValues.put(DatabaseHelper.strUnickReference, strUnickReference);
        contentValues.put(DatabaseHelper.strPickingNickname, strPickingNickname);

        long id = database.insert(DatabaseHelper.REFERENCE_TABLE_NAME, null, contentValues);
        return id;
    }

    //get Reference
    public List<RefModel> getRefById(int userID) {

        refList.clear();
        SQLiteDatabase database = helper.getWritableDatabase();
        //select * from tableName where name = ? and customerName = ?:
        // String selection = DatabaseHelper.USER_NAME+" where ? AND "+DatabaseHelper.CUSTOMER_NAME+" LIKE ?";
        String selection = DatabaseHelper.userId + "=?";

        String[] args = {""+userID};
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.intAutoPickingHeader,DatabaseHelper.userId,
                DatabaseHelper.strUnickReference,DatabaseHelper.strPickingNickname
        };

        // Cursor cursor = database.query(DatabaseHelper.PLAN_TABLE_NAME, columns, selection, args, null, null, null);
        Cursor cursor = database.query(DatabaseHelper.REFERENCE_TABLE_NAME, columns, selection, args, null, null, null);
        while (cursor.moveToNext()) {
            RefModel model = new RefModel(
                    cursor.getInt(1),
                    cursor.getString(3),
                    cursor.getString(4)
            );
            refList.add(model);
        }
        return refList;
    }


    //get PLAN by reference No:
    public List<PlanModel> getPlans() {

        planList.clear();
        SQLiteDatabase database = helper.getWritableDatabase();
        //select * from tableName where name = ? and customerName = ?:
        // String selection = DatabaseHelper.USER_NAME+" where ? AND "+DatabaseHelper.CUSTOMER_NAME+" LIKE ?";
        String selection = DatabaseHelper.IP + "=?";
//                " and " + DatabaseHelper.DATE + "=?" +
//                " and " + DatabaseHelper.ROUTE_NAME + "=?" +
//                " and " + DatabaseHelper.ORDER_TYPES + "=?" +
//                " and " + DatabaseHelper.userId + "=?";


        String[] args = {Constant.BASE_URL};
        String[] columns = {DatabaseHelper.UID, DatabaseHelper.intAutoPicking, DatabaseHelper.Storename, DatabaseHelper.Quantity,
                DatabaseHelper.ItemCode, DatabaseHelper.Description, DatabaseHelper.SalesOrderNo, DatabaseHelper.OrderId,
                DatabaseHelper.mass, DatabaseHelper.LineNos, DatabaseHelper.weights, DatabaseHelper.OrderDate,
                DatabaseHelper.Instruction, DatabaseHelper.Area, DatabaseHelper.Toinvoice, DatabaseHelper.toLoad,
                DatabaseHelper.FLAG, DatabaseHelper.IP, DatabaseHelper.reference};

        // Cursor cursor = database.query(DatabaseHelper.PLAN_TABLE_NAME, columns, selection, args, null, null, null);
        Cursor cursor = database.query(DatabaseHelper.PLAN_TABLE_NAME, columns, selection, args, null, null, null);
        while (cursor.moveToNext()) {
            PlanModel model = new PlanModel(
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getInt(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getString(13),
                    cursor.getString(14),
                    cursor.getString(15),
                    cursor.getInt(16),
                    cursor.getString(17),
                    cursor.getString(18)
            );
            planList.add(model);
        }
        return planList;
    }

    //Drop plan Table:
    public void dropPlanTable() {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL(DatabaseHelper.DROP_PLANS_TABLE);
        database.execSQL(DatabaseHelper.CREATE_PLANS_TABLE);
    }

    //Drop plan Table:
    public void dropRefTable() {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL(DatabaseHelper.DROP_REFERENCE_TABLE);
        database.execSQL(DatabaseHelper.CREATE_REF_TABLE);
    }


    // Update Quantity by name and reference code:
    public long updatePlanToLoad(String itemName, String referenceCode, String quantity, String storeName, int lineNo, int flag) {
        SQLiteDatabase database = helper.getWritableDatabase();
//        String selection = DatabaseHelper.Description + " LIKE ? AND " + DatabaseHelper.reference + " LIKE ? AND " +
//                DatabaseHelper.Storename + " LIKE ? AND " + DatabaseHelper.LineNos + " LIKE ? ";
//        String[] args = {itemName, referenceCode, storeName, "" + lineNo};

        String selection = DatabaseHelper.Description + " LIKE ? AND " +
                DatabaseHelper.Storename + " LIKE ? AND " + DatabaseHelper.LineNos + " LIKE ? ";
        String[] args = {itemName, storeName, "" + lineNo};

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.toLoad, quantity);
        contentValues.put(DatabaseHelper.FLAG, flag);

        long ids = database.update(DatabaseHelper.PLAN_TABLE_NAME, contentValues, selection, args);

        return ids;
    }

    //Update Flag by Line No.
    // Update Quantity by name and reference code:
    public long updatePlanByLine(int lineNo, int flag) {
        SQLiteDatabase database = helper.getWritableDatabase();
        String selection = DatabaseHelper.LineNos + " LIKE ? ";
        String[] args = {"" + lineNo};

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FLAG, flag);


        long ids = database.update(DatabaseHelper.PLAN_TABLE_NAME, contentValues, selection, args);

        return ids;
    }


    class DatabaseHelper extends SQLiteOpenHelper {
        private Context context;

        private static final String DATABASE_NAME = "picking_N_plan.db";
        private static final int VERSION_NUMBER = 12;

        //Header Table:
        private static final String PLAN_TABLE_NAME = "plans";
        private static final String UID = "_id";
        private static final String intAutoPicking = "intAutoPicking";
        private static final String Storename = "Storename";
        private static final String Quantity = "Quantity";
        private static final String ItemCode = "ItemCode";
        private static final String Description = "Description";
        private static final String SalesOrderNo = "SalesOrderNo";
        private static final String OrderId = "OrderId";
        private static final String mass = "mass";
        private static final String LineNos = "LineNos";
        private static final String weights = "weights";
        private static final String OrderDate = "OrderDate";
        private static final String Instruction = "Instruction";
        private static final String Area = "Area";
        private static final String Toinvoice = "Toinvoice";
        private static final String toLoad = "toLoad";
        private static final String FLAG = "flag";
        private static final String IP = "ip";
        private static final String reference = "reference";

        //Creating the table:
        private static final String CREATE_PLANS_TABLE = "CREATE TABLE " + PLAN_TABLE_NAME
                + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + intAutoPicking + " INTEGER,"
                + Storename + " VARCHAR(255),"
                + Quantity + " VARCHAR(255),"
                + ItemCode + " VARCHAR(255),"
                + Description + " VARCHAR(255),"
                + SalesOrderNo + " VARCHAR(255),"
                + OrderId + " INTEGER,"
                + mass + " VARCHAR(255),"
                + LineNos + " INTEGER,"
                + weights + " VARCHAR(255),"
                + OrderDate + " VARCHAR(255),"
                + Instruction + " VARCHAR(255),"
                + Area + " VARCHAR(255),"
                + Toinvoice + " VARCHAR(255),"
                + toLoad + " VARCHAR(255),"
                + FLAG + " INTEGER,"
                + IP + " VARCHAR(255),"
                + reference + " VARCHAR(255));";
        private static final String DROP_PLANS_TABLE = "DROP TABLE IF EXISTS " + PLAN_TABLE_NAME;

        //Ref Table:
        private static final String REFERENCE_TABLE_NAME = "ref";
        private static final String intAutoPickingHeader = "intAutoPickingHeader";
        private static final String strUnickReference = "strUnickReference";
        private static final String strPickingNickname = "strPickingNickname";
        private static final String userId = "userId";

        //Creating the table:
        private static final String CREATE_REF_TABLE = "CREATE TABLE " + REFERENCE_TABLE_NAME
                + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + intAutoPickingHeader + " INTEGER,"
                + userId + " INTEGER,"
                + strUnickReference + " VARCHAR(255),"
                + strPickingNickname + " VARCHAR(255));";
        private static final String DROP_REFERENCE_TABLE = "DROP TABLE IF EXISTS " + REFERENCE_TABLE_NAME;


        public DatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, VERSION_NUMBER);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Create table:
            try {
                db.execSQL(CREATE_PLANS_TABLE);
                db.execSQL(CREATE_REF_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_PLANS_TABLE);
                db.execSQL(DROP_REFERENCE_TABLE);
                onCreate(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}