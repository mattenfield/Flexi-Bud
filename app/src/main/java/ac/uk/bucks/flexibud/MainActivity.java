package ac.uk.bucks.flexibud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
//view widgets
    ProgressBar progressBar;
    EditText email;
    EditText password,confpassword;
    Button signin;
    Button signup;
    FirebaseAuth firebaseAuth;
    TextView costvalue;
    TextView budgetvalue;
    TextView overview;
    EditText todaysbudget,rent,food,amenities,disposable,mysetbudget;
    Button weeklycostcalculator;
    Button setbudget,savebudget;
    Button logout;
    Button back1;
    Button back2, calculatebtn;
    Button btnConfirm;
    String emailAddress;
    DatabaseReference dbref;
    UserBudget userbudget;
    Double totalweeklycost=0.0;
    String userId;
    String budget;
    String budgetremainder;
    String calculatedcost;
    String datebudget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        signin = findViewById(R.id.btnSignIn);
        signup = findViewById(R.id.btnSignUp);
        confpassword=findViewById(R.id.etConfPassword);
        btnConfirm=findViewById(R.id.btnConfirm);
        userbudget = new UserBudget();
        dbref = FirebaseDatabase.getInstance().getReference().child("UserBudget");

        firebaseAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confpassword.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
                signup.setVisibility(View.INVISIBLE);

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pword = password.getText().toString();
                String cpword = confpassword.getText().toString();

                if(pword.equals(cpword)){
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);

                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                                password.setText("");
                                confpassword.setText("");
                                confpassword.setVisibility(View.INVISIBLE);
                                btnConfirm.setVisibility(View.INVISIBLE);
                                signin.setVisibility(View.VISIBLE);
                                signup.setVisibility(View.VISIBLE);
                            }
                            else{
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }


                        }

                    });
                }
                else {
                    Toast.makeText(MainActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                }

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                emailAddress=email.getText().toString();
                userbudget.setUserName(emailAddress);
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        userId=user.getUid();
                        retrieveData();

                        if(task.isSuccessful()){
                            setContentView(R.layout.main_menu);
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            MenuListen();

                        }
                        else{
                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }

                });

            }
        });

    }
    public void MenuListen(){

        todaysbudget = findViewById(R.id.etTodayExpenditure);
        weeklycostcalculator = findViewById(R.id.btnWeeklyCosts);
        setbudget = findViewById(R.id.btnSetMyBudget);
        logout = findViewById(R.id.btnLogOut);

        weeklycostcalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.weekly_cost_calculator);
                CostCalculatorListen();
            }
        });
        setbudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.set_budget);
                BudgetListen();

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.getInstance().signOut();
                finish();
            }
        });
    }
    public void BudgetListen(){
        back1 = findViewById(R.id.btnBack);
        savebudget = findViewById(R.id.btnSaveBudget);
        mysetbudget = findViewById(R.id.etMySetBudget);
        back1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.main_menu);
                    MenuListen();
                }
        });
        savebudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myBudget = mysetbudget.getText().toString();
                if(isNumeric(myBudget)){
                    double doubleBudget = Double.valueOf(myBudget);
                    userbudget.setSetBudget(doubleBudget);
                    userbudget.setRemainingBudget(doubleBudget);
                    Date currentTime = Calendar.getInstance().getTime();
                    userbudget.setDateofBudgetSet(currentTime);
                    dbref.child(userId).setValue(userbudget);
                    Toast.makeText(MainActivity.this, "Your budget was successfully set to £" + userbudget + ".", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void CostCalculatorListen(){
        back2 = findViewById(R.id.btnBack2);
        calculatebtn = findViewById(R.id.btnCostSubmit);


        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.main_menu);
                MenuListen();
            }
        });
        calculatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rent = findViewById(R.id.etCost1);
                food = findViewById(R.id.etCost2);
                amenities = findViewById(R.id.etCost3);
                disposable = findViewById(R.id.etCost4);
                costvalue = findViewById(R.id.weeklycostlbl);

                String[] etArray = new String[4];

                etArray[0] = rent.getText().toString();
                etArray[1] = food.getText().toString();
                etArray[2] = amenities.getText().toString();
                etArray[3] = disposable.getText().toString();
//

                if (isNumeric(etArray[0])&&isNumeric(etArray[1])&&isNumeric(etArray[2])&&isNumeric(etArray[3])) {
                    for (int i = 0; i < 4; i++) {
                        String temp = etArray[i];
                         double convTemp = Double.valueOf(temp);
                        totalweeklycost = totalweeklycost + convTemp;
                    }
                    costvalue.setText("£" + totalweeklycost);
                    userbudget.setCalculatedCost(totalweeklycost);
                    //save weekly cost code.
                    totalweeklycost = 0.0;
                }
                else{
                    Toast.makeText(MainActivity.this, "Error: Values are not numeric.", Toast.LENGTH_LONG).show();
                }



            }
        });

    }
    public static boolean isNumeric(final String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        try {

            Double.parseDouble(str);
            return true;

        } catch (NumberFormatException e) {
            return false;
        }

    }
    public void retrieveData(){
        dbref = FirebaseDatabase.getInstance().getReference().child("UserBudget").child(userId);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    budget = dataSnapshot.child("setBudget").getValue().toString();
                    budgetremainder = dataSnapshot.child("remainingBudget").getValue().toString();
                    calculatedcost = dataSnapshot.child("calculatedCost").getValue().toString();
                    datebudget = dataSnapshot.child("dateofBudgetSet").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error Receiving Data.", Toast.LENGTH_LONG).show();
            }
        });
    }

}


