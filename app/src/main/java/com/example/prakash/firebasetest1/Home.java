package com.example.prakash.firebasetest1;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Queue;

public class Home extends AppCompatActivity {

    private RecyclerView bList;
    private DatabaseReference dr;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    SearchView sv;
    Book B;
    ImageView close;

    ///////// ADD BOOK ///

    EditText aname,bname,des,edi;
    TextView bid;
    Long maxid;
    LinearLayout adder;
    Button add;
    boolean isUp;

    //////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ///// ADDER ELEMENTS ///////

        aname =(EditText)findViewById(R.id.aname);
        bname = (EditText)findViewById(R.id.bname);
        des =(EditText)findViewById(R.id.des);
        edi =(EditText)findViewById(R.id.edi);
        bid = (TextView) findViewById(R.id.bid);
        adder = (LinearLayout) findViewById(R.id.adder);
        close = (ImageView)findViewById(R.id.close);
        add = (Button)findViewById(R.id.add);

        adder.setVisibility(View.INVISIBLE);
        isUp = false;

        //////////////////////////////////////////////////

        //// HANDLING CLOSE FOR ADD BOOK ///////////////

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bList.setVisibility(View.VISIBLE);
                adder.clearAnimation();
                adder.setVisibility(View.GONE);
                aname.setText("");
                bname.setText("");
                des.setText("");
                edi.setText("");
                add.setText("Add Book");
            }
        });

        ////////////////////////////////////////////////// NAVIGATION BAR ////////////////////////////////////////////////////////////////

        BottomNavigationView bvn = (BottomNavigationView) findViewById(R.id.bnb);

        bvn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        bList.setVisibility(View.VISIBLE);
                        sv.setIconified(true);
                        adder.clearAnimation();
                        adder.setVisibility(View.GONE);
                       onStart();
                       break;

                    case R.id.search:
                        bList.setVisibility(View.VISIBLE);
                        sv.setIconified(false);
                        adder.clearAnimation();
                        adder.setVisibility(View.GONE);
                        break;

                    case R.id.add:
                        sv.setIconified(true);
                        if (isUp) {
                            slideDown(adder);
                            adder.clearAnimation();
                            adder.setVisibility(View.GONE);
                            bList.setVisibility(View.VISIBLE);
                        } else {
                            slideUp(adder);
                            bList.setVisibility(View.INVISIBLE);
                        }
                        isUp = !isUp;
                        break;

                     case R.id.fav:
                         bList.setVisibility(View.VISIBLE);
                         sv.setIconified(true);
                         adder.clearAnimation();
                         adder.setVisibility(View.GONE);
                         loadFav();
                        break;

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Home.this,"Logged Out",Toast.LENGTH_LONG).show();
                        Intent lo = new Intent (Home.this, Login.class);
                        startActivity(lo);
                        finish();
                        break;
                }
                return true;
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        dr=FirebaseDatabase.getInstance().getReference().child("Books List");
        dr.keepSynced(true);

        B= new Book();

        /////////////////////// FINDING MAX ID /////////////////////////////

        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                maxid=(dataSnapshot.getChildrenCount());
                bid.setText(String.valueOf(maxid+1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /////////////////////////////////////////////////////////////


        bList=(RecyclerView)findViewById(R.id.recyclerv);
        bList.setHasFixedSize(true);
        bList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onBackPressed() {
        bList.setVisibility(View.VISIBLE);
        adder.clearAnimation();
        adder.setVisibility(View.GONE);
        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPage();
    }

    ///////////////////////////////////////////////////////////// LOADING HOME PAGE ///////////////////////////////////////////////////////////

    public void loadPage (){
        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(dr, Book.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final BookViewHolder holder, int position, @NonNull final Book model) {


                // animation is here :

                holder.fl.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

                holder.tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

                holder.setBid(model.getBid());
                holder.setDes(model.getDes());
                holder.setEdision(model.getEdision());
                holder.setBname(model.getBname());
                holder.setAname(model.getAname());

                ImageView send = holder.itemView.findViewById(R.id.send_it);
                final ImageView Add_fav = holder.itemView.findViewById(R.id.add_fav);
                final ImageView rem_fav = holder.itemView.findViewById(R.id.rem_fav);
                final ImageView delete = holder.itemView.findViewById(R.id.delete);
                final ImageView edit = holder.itemView.findViewById(R.id.editt);

                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete.setVisibility(View.GONE);
                        edit.setVisibility(View.VISIBLE);
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bList.setVisibility(View.INVISIBLE);
                        adder.setVisibility(View.VISIBLE);
                        slideUp(adder);
                        add.setText("Update Node");
                        bid.setText(model.getBid());
                        aname.setText(model.getAname());
                        bname.setText(model.getBname());
                        des.setText(model.getDes());
                        edi.setText(model.getEdision());
                    }
                });

                holder.pop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Description : "+model.getDes(),Toast.LENGTH_LONG).show();
                    }
                });

                if(model.getIsFav()!= null && model.getIsFav().equals("0")) {
                    Add_fav.setVisibility(View.GONE);
                    rem_fav.setVisibility(View.VISIBLE);
                }
                else if (model.getIsFav()!= null && model.getIsFav().equals("1")) {
                    rem_fav.setVisibility(View.GONE);
                    Add_fav.setVisibility(View.VISIBLE);
                }

                holder.fl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        edit.setVisibility(View.GONE);
                        delete.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

                holder.fl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dr.child(model.getBid()).setValue(null);
                    }
                });


                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(android.content.Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Send Book det");
                        i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey there check this out !! \n Prakash shared Book : "+model.getBname()+"\n By " +model.getAname()+ "\n of Edition : "+model.getEdision()+"\n which is a : "+ model.getDes());
                        startActivity(Intent.createChooser(i,"Share via"));
                    }
                });


                Add_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        B.setAname(model.getAname());
                        B.setBname(model.getBname());
                        B.setDes(model.getDes());
                        B.setBid(model.getBid());
                        B.setEdision(model.getEdision());

                        if(model.getIsFav()!= null && model.getIsFav().equals("1")) {
                            B.setIsFav("0");
                            dr.child(model.getBid()).setValue(B);
                        }
                        else if (model.getIsFav()!= null && model.getIsFav().equals("0")){
                            B.setIsFav("1");
                            dr.child(model.getBid()).setValue(B);
                        }
                    }
                });

                rem_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        B.setAname(model.getAname());
                        B.setBname(model.getBname());
                        B.setDes(model.getDes());
                        B.setBid(model.getBid());
                        B.setEdision(model.getEdision());

                        if(model.getIsFav()!= null && model.getIsFav().equals("1")) {
                            B.setIsFav("0");
                            dr.child(model.getBid()).setValue(B);
                        }
                        else if (model.getIsFav()!= null && model.getIsFav().equals("0")){
                            B.setIsFav("1");
                            dr.child(model.getBid()).setValue(B);
                        }
                    }
                });

            }



            @NonNull
            @Override
            public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycle_card, viewGroup, false);
                return new BookViewHolder(view);
            }

        };

        firebaseRecyclerAdapter.startListening();
        bList.setAdapter(firebaseRecyclerAdapter);

    }


    ///////////////////////////////////////////////////////////////// BOOK LIST HOLDER CLASS ////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static class BookViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        CardView fl;
        LinearLayout fL;
        TextView tv,pop;

        public BookViewHolder(View itemView){
            super(itemView);
            mview=itemView;
            fl = (CardView) itemView.findViewById(R.id.container);
            fL = (LinearLayout)itemView.findViewById(R.id.wrapper);
            tv = (TextView)itemView.findViewById(R.id.book_id);
            pop = (TextView)itemView.findViewById(R.id.book_descr);

        }
       // ImageView add_fav =(ImageView)mview.findViewById(R.id.add_fav);

        public String setisFav(String isFAv) {
            return isFAv;
        }

            public void setBid(String bid) {
            TextView book_id = (TextView)mview.findViewById(R.id.book_id);
            book_id.setText(bid);
        }

        public void setAname(String aname) {
            TextView book_auth = (TextView)mview.findViewById(R.id.book_auth);
            book_auth.setText(aname);
        }

        public void setBname(String bname) {
            TextView book_name = (TextView)mview.findViewById(R.id.book_name);
            book_name.setText(bname);
        }

        public void setDes(String des) {
            TextView book_desc = (TextView)mview.findViewById(R.id.book_descr);
            book_desc.setText(des);
        }

        public void setEdision(String edition) {
            TextView book_edition = (TextView)mview.findViewById(R.id.book_edition);
            book_edition.setText(edition);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////  MANAGING SEARCH BAR  /////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.top_menu,menu);
        MenuItem item = menu.findItem(R.id.search);
        // add filter here
        sv = (SearchView)item.getActionView();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!s.toString().isEmpty())
                    search(s);
                else
                    search("");

                return false;
            }


        });
                    /////////////////////////////////////// on click on back button activity /////////////////////////////////////////////

                            MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                                @Override
                                public boolean onMenuItemActionExpand(MenuItem item) {

                                    sv.setBackgroundResource(R.drawable.text_circle);
                                         return true;
                                }

                                @Override
                                public boolean onMenuItemActionCollapse(MenuItem item) {
                                    sv.setIconified(true);
                                    loadPage();
                                    //Toast.makeText(Home.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            });

        return super.onCreateOptionsMenu(menu);
    }

    //////////////////////////////////////////////////////////////////////////////////// PROCESSING SEARCH //////////////////////////////////////////////////////////////////////////////////////////////////

 public void search(final String s){
     Query Q = dr.orderByChild("bname")
             .startAt(s)
             .endAt(s+ "\uf8ff");

     FirebaseRecyclerOptions<Book> options =
             new FirebaseRecyclerOptions.Builder<Book>()
                     .setQuery(Q, Book.class)
                     .build();



     firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Book, BookViewHolder>(options) {
         @Override
         protected void onBindViewHolder(@NonNull final BookViewHolder holder, int position, @NonNull final Book model) {

             // animation is here :

             holder.fl.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

             holder.tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

             holder.setBid(model.getBid());
             holder.setDes(model.getDes());
             holder.setEdision(model.getEdision());
             holder.setBname(model.getBname());
             holder.setAname(model.getAname());

             ImageView send = holder.itemView.findViewById(R.id.send_it);
             final ImageView Add_fav = holder.itemView.findViewById(R.id.add_fav);
             final ImageView rem_fav = holder.itemView.findViewById(R.id.rem_fav);
             final ImageView delete = holder.itemView.findViewById(R.id.delete);
             final ImageView edit = holder.itemView.findViewById(R.id.editt);

             holder.tv.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     delete.setVisibility(View.GONE);
                     edit.setVisibility(View.VISIBLE);
                 }
             });

             edit.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     bList.setVisibility(View.INVISIBLE);
                     adder.setVisibility(View.VISIBLE);
                     slideUp(adder);
                     add.setText("Update Node");
                     bid.setText(model.getBid());
                     aname.setText(model.getAname());
                     bname.setText(model.getBname());
                     des.setText(model.getDes());
                     edi.setText(model.getEdision());
                 }
             });

             holder.pop.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Toast.makeText(getApplicationContext(),"Description : "+model.getDes(),Toast.LENGTH_LONG).show();
                 }
             });

             if(model.getIsFav()!= null && model.getIsFav().equals("0")) {
                 Add_fav.setVisibility(View.GONE);
                 rem_fav.setVisibility(View.VISIBLE);
             }
             else if (model.getIsFav()!= null && model.getIsFav().equals("1")) {
                 rem_fav.setVisibility(View.GONE);
                 Add_fav.setVisibility(View.VISIBLE);
             }

             holder.fl.setOnLongClickListener(new View.OnLongClickListener() {
                 @Override
                 public boolean onLongClick(View v) {
                     edit.setVisibility(View.GONE);
                     delete.setVisibility(View.VISIBLE);
                     return true;
                 }
             });

             holder.fl.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     edit.setVisibility(View.GONE);
                     delete.setVisibility(View.GONE);
                 }
             });

             delete.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     dr.child(model.getBid()).setValue(null);
                 }
             });


             send.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent i=new Intent(android.content.Intent.ACTION_SEND);
                     i.setType("text/plain");
                     i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Send Book det");
                     i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey there check this out !! \n Prakash shared Book : "+model.getBname()+"\n By " +model.getAname()+ "\n of Edition : "+model.getEdision()+"\n which is a : "+ model.getDes());
                     startActivity(Intent.createChooser(i,"Share via"));
                 }
             });


             Add_fav.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     B.setAname(model.getAname());
                     B.setBname(model.getBname());
                     B.setDes(model.getDes());
                     B.setBid(model.getBid());
                     B.setEdision(model.getEdision());

                     if(model.getIsFav()!= null && model.getIsFav().equals("1")) {
                         B.setIsFav("0");
                         dr.child(model.getBid()).setValue(B);
                     }
                     else if (model.getIsFav()!= null && model.getIsFav().equals("0")){
                         B.setIsFav("1");
                         dr.child(model.getBid()).setValue(B);
                     }
                 }
             });

             rem_fav.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     B.setAname(model.getAname());
                     B.setBname(model.getBname());
                     B.setDes(model.getDes());
                     B.setBid(model.getBid());
                     B.setEdision(model.getEdision());

                     if(model.getIsFav()!= null && model.getIsFav().equals("1")) {
                         B.setIsFav("0");
                         dr.child(model.getBid()).setValue(B);
                     }
                     else if (model.getIsFav()!= null && model.getIsFav().equals("0")){
                         B.setIsFav("1");
                         dr.child(model.getBid()).setValue(B);
                     }
                 }
             });

         }

         @NonNull
         @Override
         public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
             View view = LayoutInflater.from(viewGroup.getContext())
                     .inflate(R.layout.recycle_card, viewGroup, false);
             return new BookViewHolder(view);
         }

     };

     firebaseRecyclerAdapter.startListening();
     bList.setAdapter(firebaseRecyclerAdapter);
 }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////// LOADING FAV BOOKS LIST ALONE /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadFav(){

        Query Q = dr.orderByChild("isFav").equalTo("1");

        FirebaseRecyclerOptions<Book> options =
                new FirebaseRecyclerOptions.Builder<Book>()
                        .setQuery(Q, Book.class)
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Book, Home.BookViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Home.BookViewHolder holder, int position, @NonNull final Book model) {

                // animation is here :

                holder.fl.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

                holder.tv.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_animation));

                holder.setBid(model.getBid());
                holder.setDes(model.getDes());
                holder.setEdision(model.getEdision());
                holder.setBname(model.getBname());
                holder.setAname(model.getAname());

                ImageView send = holder.itemView.findViewById(R.id.send_it);
                final ImageView Add_fav = holder.itemView.findViewById(R.id.add_fav);
                final ImageView rem_fav = holder.itemView.findViewById(R.id.rem_fav);
                final ImageView delete = holder.itemView.findViewById(R.id.delete);
                final ImageView edit = holder.itemView.findViewById(R.id.editt);

                holder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete.setVisibility(View.GONE);
                        edit.setVisibility(View.VISIBLE);
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bList.setVisibility(View.INVISIBLE);
                        adder.setVisibility(View.VISIBLE);
                        slideUp(adder);
                        add.setText("Update Node");
                        bid.setText(model.getBid());
                        aname.setText(model.getAname());
                        bname.setText(model.getBname());
                        des.setText(model.getDes());
                        edi.setText(model.getEdision());
                    }
                });

                holder.pop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"Description : "+model.getDes(),Toast.LENGTH_LONG).show();
                    }
                });

                if(model.getIsFav()!= null && model.getIsFav().equals("0")) {
                    Add_fav.setVisibility(View.GONE);
                    rem_fav.setVisibility(View.VISIBLE);
                }
                else if (model.getIsFav()!= null && model.getIsFav().equals("1")) {
                    rem_fav.setVisibility(View.GONE);
                    Add_fav.setVisibility(View.VISIBLE);
                }

                holder.fl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        edit.setVisibility(View.GONE);
                        delete.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

                holder.fl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edit.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dr.child(model.getBid()).setValue(null);
                    }
                });


                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(android.content.Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Send Book det");
                        i.putExtra(android.content.Intent.EXTRA_TEXT, "Hey there check this out !! \n Prakash shared Book : "+model.getBname()+"\n By " +model.getAname()+ "\n of Edition : "+model.getEdision()+"\n which is a : "+ model.getDes());
                        startActivity(Intent.createChooser(i,"Share via"));
                    }
                });


                Add_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        B.setAname(model.getAname());
                        B.setBname(model.getBname());
                        B.setDes(model.getDes());
                        B.setBid(model.getBid());
                        B.setEdision(model.getEdision());

                        if(model.getIsFav()!= null && model.getIsFav().equals("1")) {
                            B.setIsFav("0");
                            dr.child(model.getBid()).setValue(B);
                        }
                        else if (model.getIsFav()!= null && model.getIsFav().equals("0")){
                            B.setIsFav("1");
                            dr.child(model.getBid()).setValue(B);
                        }
                    }
                });

                rem_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        B.setAname(model.getAname());
                        B.setBname(model.getBname());
                        B.setDes(model.getDes());
                        B.setBid(model.getBid());
                        B.setEdision(model.getEdision());

                        if(model.getIsFav()!= null && model.getIsFav().equals("1")) {
                            B.setIsFav("0");
                            dr.child(model.getBid()).setValue(B);
                        }
                        else if (model.getIsFav()!= null && model.getIsFav().equals("0")){
                            B.setIsFav("1");
                            dr.child(model.getBid()).setValue(B);
                        }
                    }
                });

            }

            @NonNull
            @Override
            public Home.BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.recycle_card, viewGroup, false);
                return new Home.BookViewHolder(view);
            }

        };

        firebaseRecyclerAdapter.startListening();
        bList.setAdapter(firebaseRecyclerAdapter);


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////// HANDLING ADD BOOK EVENT ////////////////////////////

    public void insert(View view){

        if (add.getText().toString().equals("Update Node")) {
            Update();
        }
        else {
            String an = aname.getText().toString();
            String bn = bname.getText().toString();
            String de = des.getText().toString();
            String ed = edi.getText().toString();

            if (!an.isEmpty() && !bn.isEmpty() && !de.isEmpty() && !ed.isEmpty()) {

                B.setAname(an);
                B.setBname(bn);
                B.setDes(de);
                B.setBid(String.valueOf(maxid + 1));
                B.setEdision(ed);
                B.setIsFav("0");
                dr.child(String.valueOf(maxid + 1)).setValue(B);

                Toast.makeText(this, "BOOK ADDED SUCCESSFULLY !!", Toast.LENGTH_LONG).show();

                aname.setText("");
                bname.setText("");
                des.setText("");
                edi.setText("");

                bList.setVisibility(View.VISIBLE);
                adder.clearAnimation();
                adder.setVisibility(View.GONE);
            }
            else {
                bList.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Please Fill all info !!", Toast.LENGTH_LONG).show();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////// ADD BOOK SLIDE UP AND DOWN FUNC() ////////////////////////////////////////

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                800); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);


    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////// UPDATE FUNCTION /////////////////////////////////////

    public void Update(){
        String an = aname.getText().toString();
        String bn = bname.getText().toString();
        String de = des.getText().toString();
        String ed = edi.getText().toString();
        String id = bid.getText().toString();

        if(!an.isEmpty() && !bn.isEmpty() && !de.isEmpty() && !ed.isEmpty() && !id.isEmpty()){

            B.setAname(an);
            B.setBname(bn);
            B.setDes(de);
            B.setBid(id);
            B.setEdision(ed);
            B.setIsFav("0");
            dr.child(id).setValue(B);

            Toast.makeText(this,"BOOK DETAILS UPDATED SUCCESSFULLY !!", Toast.LENGTH_LONG).show();

            aname.setText("");
            bname.setText("");
            des.setText("");
            edi.setText("");
            add.setText("Add Book");

            bList.setVisibility(View.VISIBLE);
            adder.clearAnimation();
            adder.setVisibility(View.GONE);
        }
        else {
            bList.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please Fill all info !!", Toast.LENGTH_LONG).show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

}
