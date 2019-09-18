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
import com.example.android.eatitserver.Model.Banner;
import com.example.android.eatitserver.Model.Food;
import com.example.android.eatitserver.ViewHolder.BannerViewHolder;
import com.example.android.eatitserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class BannerActivity extends AppCompatActivity {
    // For Data Base.
    FirebaseDatabase database;
    // To Get Reference Images Of Food.
    DatabaseReference banners;

    // RecyclerView widget is a more advanced and flexible version of ListView.
    RecyclerView recyclerBanner;
    // A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
    RecyclerView.LayoutManager layoutManager;

    // For Storage.
    FirebaseStorage storage;

    // Get Reference Of Storage.
    StorageReference storageReference;

    // When Click On addFood Btn Into Food Menu.
    CounterFab addBanner;

    // Need Layout To Snack Bar.
    RelativeLayout bannerLayout;

    // Make It Public To Use It In Every Where.
    FirebaseRecyclerAdapter<Banner, BannerViewHolder> adapter;

    // For Alert Dialog To Add New Banner.
    MaterialEditText bannerFoodName, bannerFoodId;
    // Buttons To Make Change.
    FButton btnSelect, btnUpload;

    // Constant.
    private final int PICK_IMAGE_REQUEST = 71;

    // Get Banner Class.
    Banner newBanner;

    // Get Uri Of Image From FireBase.
    Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        // Init FireBase.
        database = FirebaseDatabase.getInstance();
        // Reference Or Name Of Our DataBase [Root], In This Case Called Banner.
        banners = database.getReference("Banner");

        // Init FireBase Storage.
        storage = FirebaseStorage.getInstance();
        // Reference Or Name Of Our Storage [Root].
        storageReference = storage.getReference("images/");

        // Load Menu.
        // To Get Image One By One.
        recyclerBanner = (RecyclerView) findViewById(R.id.recycler_banner);
        // Make All Has The Same Size.
        recyclerBanner.setHasFixedSize(true);

        // Init layoutManager As A ListView
        // As My ListOfCall Program, Was Need To List View And Adapter.
        layoutManager = new LinearLayoutManager(this);
        // Set recycler_menu As A ListView.
        recyclerBanner.setLayoutManager(layoutManager);

        bannerLayout = (RelativeLayout) findViewById(R.id.banner_layout);

        addBanner = (CounterFab) findViewById(R.id.add_banner);
        addBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call showAddBannerDialog Method.
                showAddBannerDialog();
            }
        });

        // Call Method.
        loadListBanner();

    }

    // When Call Method.
    private void loadListBanner() {
        // Update To FirebaseUI 3.2.2.
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Banner>()
                .setQuery(banners, Banner.class).build();

        adapter = new FirebaseRecyclerAdapter<Banner, BannerViewHolder>(options) {

            @Override
            public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.banner_layout, parent, false);
                return new BannerViewHolder(itemView);
            }

            // Make List Of Recycler Banners By Name & Image From FireBase DataBase.
            @Override
            protected void onBindViewHolder(@NonNull BannerViewHolder holder, int position, @NonNull Banner model) {
                // Set Name Of Banner.
                holder.bannerName.setText(model.getName());
                // Set Img Of Banner.
                Picasso.with(getBaseContext()).load(model.getImage()).into(holder.bannerImg);

            }
        };
        // Start adapter With New Update, Without It You Will Not Be Able To Appear Your Info.
        adapter.startListening();
        // Refresh Data If Has Changed On It.
        adapter.notifyDataSetChanged();
        // After End All Of This Set Adapter To Appear It On The Device.
        recyclerBanner.setAdapter(adapter);
    }

    // When Click On addBanner Btn Call Method.
    private void showAddBannerDialog() {
        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("Add new Banner");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Please fill full information ");

        // Adapt Alert Dialog As My Azkar App.
        LayoutInflater inflater = this.getLayoutInflater();

        // Get LayOut By ID.
        View addBannerLayout = inflater.inflate(R.layout.add_new_banner_layout, null);

        bannerFoodId = addBannerLayout.findViewById(R.id.edit_id_for_new_banner);

        bannerFoodName = addBannerLayout.findViewById(R.id.edit_name_for_new_banner);

        // Get btnSelect By Id.
        btnSelect = (FButton) addBannerLayout.findViewById(R.id.btn_select);

        // Get btnUpload By Id.
        btnUpload = (FButton) addBannerLayout.findViewById(R.id.btn_upload);

        // When Click On Select Btn.
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });

        // When Click On Upload Btn.
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        // Set Alert Dialog.
        alertDialog.setView(addBannerLayout);
        alertDialog.setIcon(R.drawable.ic_laptop_black_24dp);

        // Set Button For Dialog.
        // When Click On CREATE Btn
        alertDialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (newBanner != null)
                    // [.push] Make Random Key.
                    banners.push().setValue(newBanner);

                // Call Method To Set Updated Banner.
                loadListBanner();
            }
        });

        // When Click On CANCEL Btn
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                newBanner = null;
            }
        });

        alertDialog.show();
    }

    // Choose Image From Device.
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
            filePath = data.getData();
            // Change Text Of Btn.
            btnSelect.setText("Image Selected!");
        }
    }

    private void uploadImage() {
        // If filePath Store Image.
        if (filePath != null) {
            // Show Progress Dialog.
            final ProgressDialog mDialog = new ProgressDialog(this);

            // Set Message
            mDialog.setMessage("Uploading...");

            // Show This Dialog.
            mDialog.show();

            // Store Random Name For Images, Into imgName Var.
            String imgName = UUID.randomUUID().toString();

            // Store Image When User Upload It Into Storage FireBase [foods/banners/] >> Check FireBase Storage.
            final StorageReference imgFolder = storageReference.child("banners/" + imgName);

            // Put Image Into [banners/] Folder On FireBase Storage.
            imgFolder.putFile(filePath)
                    // When Upload Is Done Perfect.
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Close Progress Dialog.
                            mDialog.dismiss();

                            // Show Message.
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            // Get Link Image From [banners/] Folder On FireBase Storage.
                            imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Set Value For New Banner If Image Uploaded And We Can Get Download Link.
                                    // Send To Constructor.
                                    newBanner = new Banner(
                                            bannerFoodId.getText().toString(),
                                            bannerFoodName.getText().toString(),
                                            uri.toString());
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
                            Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            // Call showUpdateBannerDialog Method
            // Take Two Prams [Key Of Ref Into DataBase By Position, Value Into Food Class].
            showUpdateBannerDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        // If It Equals Delete.
        else if (item.getTitle().equals(Commons.DELETE)) {
            // Call deleteBanner Method.
            // Take One Pram [Key Of Ref Into DataBase By Position].
            deleteBanner(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    // When Call Method.
    private void deleteBanner(String key) {
        // Get Value From DataBase And Remove It.
        banners.child(key).removeValue();
        Toast.makeText(this, "Item Deleted..!!", Toast.LENGTH_SHORT).show();
    }

    // When Call showUpdateBannerDialog Method.
    private void showUpdateBannerDialog(final String key, final Banner item) {

        // Make New Alert Dialog.
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BannerActivity.this);
        // Set Title Of alertDialog Message.
        alertDialog.setTitle("Edit Banner");
        // Set Message Of Alert Dialog.
        alertDialog.setMessage("Please fill full information");

        // Adapt Alert Dialog As My Azkar App.
        LayoutInflater inflater = this.getLayoutInflater();

        // Get LayOut By ID.
        View addBannerLayout = inflater.inflate(R.layout.add_new_banner_layout, null);

        // Get Id Of Banner.
        bannerFoodId = (MaterialEditText) addBannerLayout.findViewById(R.id.edit_id_for_new_banner);

        // Get Name By Id.
        bannerFoodName = (MaterialEditText) addBannerLayout.findViewById(R.id.edit_name_for_new_banner);



        // Set Old Information Into The Fields.
        bannerFoodId.setText(item.getId());

        // Set Old Information Into The Fields.
        bannerFoodName.setText(item.getName());


        // Get btnSelect By Id.
        btnSelect = (FButton) addBannerLayout.findViewById(R.id.btn_select);

        // Get btnUpload By Id.
        btnUpload = (FButton) addBannerLayout.findViewById(R.id.btn_upload);

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
                // Call changeImage Method To Allow User Upload Image To Storage FireBase.
                changeImage(item);
            }
        });

        // Add new View To Alert Dialog.
        alertDialog.setView(addBannerLayout);
        // Add Icon To Alert Dialog.
        alertDialog.setIcon(R.drawable.ic_laptop_black_24dp);

        // Make Buttons For Alert Dialog [UPDATE , CANCEL].
        // When Press On UPDATE.
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Store New Info [Item's Info From User For New Banner].
                item.setId(bannerFoodId.getText().toString());

                item.setName(bannerFoodName.getText().toString());

                // Make Update.
                Map<String, Object> update = new HashMap<>();
                update.put("id", item.getId());
                update.put("name", item.getName());
                update.put("image", item.getImage());

                banners.child(key).setValue(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // As A Toast But Advanced, Used To Show Message Too.
                                Snackbar.make(bannerLayout, "Updated", Snackbar.LENGTH_SHORT)
                                        .show();

                                // Call Method Again To Set Update.
                                loadListBanner();
                            }
                        });
                // Close Alert Dialog.
                dialogInterface.dismiss();

                // Call Method To Set Updated Banner.
                loadListBanner();

                // As A Toast But Advanced, Used To Show Message Too.
                Snackbar.make(bannerLayout, "Banner Food " + item.getName() + " was added", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        // When Press On CANCEL.
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Close Alert Dialog.
                dialogInterface.dismiss();

                // Call Method To Set Updated Banner.
                loadListBanner();
            }
        });
        // After Init All Of This, Show The Alert Dialog.
        alertDialog.show();
    }

    private void changeImage(final Banner item) {
        // If uriSaveImage Store Image.
        if (filePath != null) {
            // Show Progress Dialog.
            final ProgressDialog mDialog = new ProgressDialog(this);

            // Set Message
            mDialog.setMessage("Uploading...");

            // Show This Dialog.
            mDialog.show();

            // Store Random Name For Images, Into imgName Var.
            String imgName = UUID.randomUUID().toString();

            // Store Image When User Upload It Into Storage FireBase [foods/] >> Check FireBase Storage.
            final StorageReference imgFolder = storageReference.child("banners/" + imgName);

            // Put Image Into [foods/] Folder On FireBase Storage.
            imgFolder.putFile(filePath)
                    // When Upload Is Done Perfect.
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Close Progress Dialog.
                            mDialog.dismiss();

                            // Show Message.
                            Toast.makeText(BannerActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            // Get Link Image From [banners/] Folder On FireBase Storage.
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
                            Toast.makeText(BannerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
