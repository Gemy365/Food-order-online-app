package com.example.android.eatitserver;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.android.eatitserver.Commons.Commons;
import com.example.android.eatitserver.Interface.ItemClickListener;
import com.example.android.eatitserver.Model.Category;
import com.example.android.eatitserver.Model.Food;
import com.example.android.eatitserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class FoodList extends AppCompatActivity {

    // For Data Base.
    FirebaseDatabase database;
    // To Get Reference Images Of Food.
    DatabaseReference foodList;

    // To Store menuId From FireBase DataBase.. [Check menuId To Know It]
    String categoryId = "";

    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerFood;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    // For Storage.
    FirebaseStorage storage;

    // Get Reference Of Storage.
    StorageReference storageReference;

    // When Click On addFood Btn Into Food Menu.
    CounterFab addFood;

    // For Alert Dialog.
    MaterialEditText edtName, edtDescription, edtPrice, edtDiscount;
    FButton btnSelect, btnUpload;

    Food newFood;

    RelativeLayout rootLayout;

    // Get URI Of Image.
    Uri uriSaveImage;

    // Constant.
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Category For Images.
        foodList = database.getReference("Food");

        // Init FireBase Storage.
        storage = FirebaseStorage.getInstance();
        // Reference Or Name Of Our Storage [Root], In This Case Called [images/] For Images.
        storageReference = storage.getReference("images/");

        // Load Menu.
        // To Get Image One By One.
        recyclerFood = (RecyclerView) findViewById(R.id.recycler_food);
        // Make All Has The Same Size.
        recyclerFood.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_menu As A ListView.
        recyclerFood.setLayoutManager(layoutManager);

        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);

        addFood = (CounterFab) findViewById(R.id.add_food);
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call showAddFoodDialog Method.
                showAddFoodDialog();
            }
        });

        // Get CategoryId From Intent, It's True When You Click On Image On Menu.
        if (getIntent() != null)
            // Store ID Into categoryId.
            categoryId = getIntent().getStringExtra("CategoryId");

        // It's True.
        if (!categoryId.isEmpty())
            // Call This Method By Sending ID.
            loadListFood(categoryId);
    }

    // Called Method When Click On Add Btn.
    private void showAddFoodDialog() {

        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("Add new Food");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Please fill full information ");

        // Adapt Alert Dialog As My Azkar App.
        LayoutInflater inflater = this.getLayoutInflater();

        // Get LayOut By ID.
        View addFoodLayout = inflater.inflate(R.layout.add_new_food_layout, null);

        // Get Name By Id.
        edtName = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_name_for_new_food);

        // Get Name By Id.
        edtDescription = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_description_for_new_food);

        // Get Name By Id.
        edtPrice = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_price_for_new_food);

        // Get Name By Id.
        edtDiscount = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_discounte_for_new_food);

        // Get btnSelect By Id.
        btnSelect = (FButton) addFoodLayout.findViewById(R.id.btn_select);

        // Get btnUpload By Id.
        btnUpload = (FButton) addFoodLayout.findViewById(R.id.btn_upload);

        // When Click On btnSelect.
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call chooseImage Method To Allow User To Choose Image From His Gallery.
                chooseImage();
            }
        });

        // When Click On btnUpload.
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call uploadImage Method To Allow User Upload Image To Storage FireBase.
                uploadImage();
            }
        });

        // Add new View To Alert Dialog.
        alertDialog.setView(addFoodLayout);
        // Add Icon To Alert Dialog.
        alertDialog.setIcon(R.drawable.ic_add_menu_dark);

        // Make Buttons For Alert Dialog [Yes , No].
        // When Press On YES.
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close Alert Dialog.
                dialogInterface.dismiss();

                // Check If There's Image For Food.
                if (newFood != null) {
                    // Add New Food To Menu & To Category On FireBase DataBase By Key Equal The Name Of Pic.
                    // setValue Make Keys In LowerCase. So Don't ForGet To Change all Key's UpperCase.
                    foodList.child(edtName.getText().toString()).setValue(newFood);

                    // As A Toast But Advanced, Used To Show Message Too.
                    Snackbar.make(rootLayout, "New Food " + newFood.getName() + " was added", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        // When Press On NO.
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close Alert Dialog.
                dialogInterface.dismiss();
            }
        });
        // After Init All Of This, Show The Alert Dialog.
        alertDialog.show();
    }

    private void loadListFood(String categoryId) {
        // Update To FirebaseUI 3.2.2.
        Query listFoodByCategoryId = foodList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(listFoodByCategoryId, Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(itemView);
            }

            /**
             * @param viewHolder From FoodViewHolder Own Class.
             * @param model From Food Own Class.
             * @param position To Get Item By Position.
             */
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                // To Set Name Of Food Image Every Time.
                // model parameter Of Food Own Of This Method.
                // [model.getName() = Key] To Get Value Of Name From DataBase.
                viewHolder.txtFoodName.setText(model.getName());
                // To Load Food Image From Fire Base And Set It Into imgFoodView.
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgFoodView);

                // When Click On Image.
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
////                        // Goto FoodDetail Activity.
//                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
////                        // Cause FoodId is Key, So We Just Get Key Of This Item.
////                        // putExtra[Key, Value].
////                        // Send FoodId To FoodDetail Activity.
//                       foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
////                        // Start Activity.
//                        startActivity(foodDetail);
                    }
                });
            }
        };
        // Start adapter With New Update, Without It You Will Not Be Able To Appear Your Info.
        adapter.startListening();
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerFood.setAdapter(adapter);
    }

    // Call chooseImage Method To Allow User Choose Image From His Gallery.
    private void chooseImage() {
        // Init Intent.
        Intent intent = new Intent();

        // Type Of Thing As You Want, In This Case We Need Images.
        intent.setType("image/*");

        // Get Content From Device.
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Call startActivityForResult Method To Start Activity.
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Called From chooseImage Method.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If True.
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            // Get Data & Store It Into uriSaveImage Var.
            uriSaveImage = data.getData();
            // Change Text Of Btn.
            btnSelect.setText("Image Selected!");
        }
    }

    // Call uploadImage Method To Allow User Upload Image To Storage FireBase.
    private void uploadImage() {
        // If uriSaveImage Store Image.
        if (uriSaveImage != null) {
            // Show Progress Dialog.
            final ProgressDialog mDialog = new ProgressDialog(this);

            // Set Message
            mDialog.setMessage("Uploading...");

            // Show This Dialog.
            mDialog.show();

            // Store Random Name For Images, Into imgName Var.
            String imgName = UUID.randomUUID().toString();

            // Store Image When User Upload It Into Storage FireBase [foods/] >> Check FireBase Storage.
            final StorageReference imgFolder = storageReference.child("foods/" + imgName);

            // Put Image Into [images/] Folder On FireBase Storage.
            imgFolder.putFile(uriSaveImage)
                    // When Upload Is Done Perfect.
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Close Progress Dialog.
                            mDialog.dismiss();

                            // Show Message.
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            // Get Link Image From [images/] Folder On FireBase Storage.
                            imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Set Value For New Category If Image Uploaded And We Can Get Download Link.
                                    // Send Two Prams To Constructor [Name, Link].
                                    newFood = new Food();

                                    newFood.setName(edtName.getText().toString());
                                    newFood.setDescription(edtDescription.getText().toString());
                                    newFood.setPrice(edtPrice.getText().toString());
                                    newFood.setDiscount(edtDiscount.getText().toString());
                                    // Get New Food ID's Category To Show Into Same Menu
                                    newFood.setMenuId(categoryId);
                                    // Set Link Image From Storage FireBase.
                                    newFood.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    // When Upload Doesn't Complete.
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Close Progress Dialog.
                            mDialog.dismiss();

                            // Show Error Message To User.
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    // When Progress Of Download Appear.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Convert From double To int.
                            int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            // Show Message Of Upload ex [Uploaded 46%].
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        } else
            // Show Message To User.
            Toast.makeText(this, "Please select image first", Toast.LENGTH_SHORT).show();
    }


    // When Long Press On Image To Appear The Options.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // If It Equals Update.
        if (item.getTitle().equals(Commons.UPDATE)) {
            // Call showUpdateDialog Method
            // Take Two Prams [Key Of Ref Into DataBase By Order, Value Into Food Class].
            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        // If It Equals Delete.
        else if (item.getTitle().equals(Commons.DELETE)) {
            // Call deleteCategory Method
            // Take One Pram [Key Of Ref Into DataBase By Order].
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    // To Delete Item From Food.
    private void deleteFood(String key) {
        // Get Value From DataBase And Remove It.
        foodList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted..!!", Toast.LENGTH_SHORT).show();
    }

    // To Update The Item To Make It New Food.
    private void showUpdateFoodDialog(final String key, final Food item) {
        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("Edit Food");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Please fill full information");

        // Adapt Alert Dialog As My Azkar App.
        LayoutInflater inflater = this.getLayoutInflater();

        // Get LayOut By ID.
        View addFoodLayout = inflater.inflate(R.layout.add_new_food_layout, null);

        // Get Name By Id.
        edtName = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_name_for_new_food);

        // Get Description By Id.
        edtDescription = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_description_for_new_food);

        // Get Price By Id.
        edtPrice = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_price_for_new_food);

        // Get Discount By Id.
        edtDiscount = (MaterialEditText) addFoodLayout.findViewById(R.id.edit_discounte_for_new_food);

        // Set Old Information To Fill The Fields.
        edtName.setText(item.getName());

        // Set Old Information To Fill The Fields.
        edtDescription.setText(item.getDescription());

        // Set Old Information To Fill The Fields.
        edtPrice.setText(item.getPrice());

        // Set Old Information To Fill The Fields.
        edtDiscount.setText(item.getDiscount());

        // Get btnSelect By Id.
        btnSelect = (FButton) addFoodLayout.findViewById(R.id.btn_select);

        // Get btnUpload By Id.
        btnUpload = (FButton) addFoodLayout.findViewById(R.id.btn_upload);

        // When Click On btnSelect.
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call chooseImage Method To Allow User To Choose Image From His Gallery.
                chooseImage();
            }
        });

        // When Click On btnUpload.
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call uploadImage Method To Allow User Upload Image To Storage FireBase.
                changeImage(item);
            }
        });

        // Add new View To Alert Dialog.
        alertDialog.setView(addFoodLayout);
        // Add Icon To Alert Dialog.
        alertDialog.setIcon(R.drawable.ic_add_menu_dark);

        // Make Buttons For Alert Dialog [Yes , No].
        // When Press On YES.
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Store New Info [Item's Info From User For New Food].
                item.setName(edtName.getText().toString());

                item.setDescription(edtDescription.getText().toString());

                item.setPrice(edtPrice.getText().toString());

                item.setDiscount(edtDiscount.getText().toString());

                foodList.child(key).setValue(item);
                // Close Alert Dialog.
                dialogInterface.dismiss();

                // As A Toast But Advanced, Used To Show Message Too.
                Snackbar.make(rootLayout, "Food " + item.getName() + " was added", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        // When Press On NO.
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close Alert Dialog.
                dialogInterface.dismiss();
            }
        });
        // After Init All Of This, Show The Alert Dialog.
        alertDialog.show();
    }

    // Call changeImage Method To Allow User Upload Image To Storage FireBase.
    private void changeImage(final Food item) {
        // If uriSaveImage Store Image.
        if (uriSaveImage != null) {
            // Show Progress Dialog.
            final ProgressDialog mDialog = new ProgressDialog(this);

            // Set Message
            mDialog.setMessage("Uploading...");

            // Show This Dialog.
            mDialog.show();

            // Store Random Name For Images, Into imgName Var.
            String imgName = UUID.randomUUID().toString();

            // Store Image When User Upload It Into Storage FireBase [foods/] >> Check FireBase Storage.
            final StorageReference imgFolder = storageReference.child("foods/" + imgName);

            // Put Image Into [foods/] Folder On FireBase Storage.
            imgFolder.putFile(uriSaveImage)
                    // When Upload Is Done Perfect.
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Close Progress Dialog.
                            mDialog.dismiss();

                            // Show Message.
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            // Get Link Image From [foods/] Folder On FireBase Storage.
                            imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Set New Image For Item In Food.
                                    item.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    // When Upload Doesn't Complete.
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Close Progress Dialog.
                            mDialog.dismiss();

                            // Show Error Message To User.
                            Toast.makeText(FoodList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    // When Progress Of Download Appear.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Convert From double To int.
                            int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            // Show Message Of Upload ex [Uploaded 46%].
                            mDialog.setMessage("Uploaded " + progress + "%");
                        }
                    });
        } else
            // Show Message To User.
            Toast.makeText(this, "Please select image first", Toast.LENGTH_SHORT).show();
    }
}
