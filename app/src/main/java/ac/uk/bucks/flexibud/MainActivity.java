package ac.uk.bucks.flexibud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
//view widgets


    TextView costvalue;
    EditText todaysbudget,rent,food,amenities,disposable,mysetbudget;
    String emailaddress;
    //======MAIN ACTIVITY ========//
    ProgressBar progressBar;
    EditText email;
    EditText password,confpassword;
    Button signin;
    Button signup;
    Button cancel;

    Button btnConfirm;
    //======MAIN MENU ========//
    TextView budgetvalue,budgetremaining, setbudcost;
    Button weeklycostcalculator,setbudget,expsubmit;
    TextView overview;
    Button logout;
    EditText ettodayexp;
    String todaydate;
    Date todaysdate;
    //======COST CALCULATOR ========//
    Button back2, calculatebtn,calc;
    EditText costbudget;
    //======SET BUDGET ========//
    Button savebudget;
    Button back1;
    //======DB ========//
    DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    UserBudget userbudget;
    Double totalweeklycost=0.0;
    String userId;
    //======DATA RETRIEVAL ========//
    String calculatedcost;
    String datebudget;
    String dateexp;
    String budget;
    String budgetremainder;


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
        cancel = findViewById(R.id.btnCancel);
        userbudget = new UserBudget();
        dbref = FirebaseDatabase.getInstance().getReference().child("UserBudget");

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            setContentView(R.layout.main_menu);
            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            userId=user.getUid();
            MenuListen();
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confpassword.setVisibility(View.VISIBLE);
                btnConfirm.setVisibility(View.VISIBLE);
                signin.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pword = password.getText().toString();
                String cpword = confpassword.getText().toString();

                if(pword.equals(cpword)&&!pword.equals("")&&!cpword.equals("")&&!email.equals("")){
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
                                confpassword.setVisibility(View.GONE);
                                btnConfirm.setVisibility(View.GONE);
                                cancel.setVisibility(View.GONE);
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
                    confpassword.setError("Unfortunately your passwords did not match.");
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.setText("");
                confpassword.setText("");
                confpassword.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                signin.setVisibility(View.VISIBLE);
                signup.setVisibility(View.VISIBLE);

            }


        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailaddress=email.getText().toString();
                userbudget.setUserName(emailaddress);
                if(email.getText().toString().equals("")||password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Must not leave values blank.", Toast.LENGTH_LONG).show();
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            userId=user.getUid();


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


            }
        });

    }
    public void MenuListen(){

        todaysbudget = findViewById(R.id.etTodayExpenditure);
        weeklycostcalculator = findViewById(R.id.btnWeeklyCosts);
        expsubmit = findViewById(R.id.btnExpSubmit);
        setbudget = findViewById(R.id.btnSetMyBudget);
        logout = findViewById(R.id.btnLogOut);
        ettodayexp = findViewById(R.id.etTodayExpenditure);
        overview = findViewById(R.id.lbl_overview);
        todaysdate = Calendar.getInstance().getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        todaydate = sf.format(todaysdate);

        weeklycostcalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.weekly_cost_calculator);
                CostCalculatorListen();
            }
        });
        expsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double dblRemaining = Double.valueOf(budgetremainder);
                String todayexpstr = ettodayexp.getText().toString();

                if(!todayexpstr.equals(""))
                {
                Double dbltodaysexp = Double.valueOf(todayexpstr);


                dblRemaining = dblRemaining - dbltodaysexp;

                if(!todaydate.equals(dateexp)){

                    dbref.child("remainingBudget").setValue(dblRemaining);
                    dbref.child("dateofLastSubmission").setValue(todaysdate);

                    if(dblRemaining<0){
                        Double overspend = dblRemaining*-1;
                        overview.setText("You have overspent by £ "+ overspend);
                    }
                    else if(dblRemaining==0){
                        overview.setText("You have spent your weekly budget.");
                    }
                    else if(dblRemaining>0){
                        overview.setText("You have £" + dblRemaining + " remaining.");
                    }
                    retrieveData();

                }
                else{
                    ettodayexp.setError("You have already submitted your expenses today.");
                }

                }
                else{
                    ettodayexp.setError("You cannot leave this value blank.");
                }
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
        retrieveData();
    }
    public void BudgetListen(){
        back1 = findViewById(R.id.btnBack);
        savebudget = findViewById(R.id.btnSaveBudget);
        mysetbudget = findViewById(R.id.etMySetBudget);
        setbudcost = findViewById(R.id.setbudcost);
        if(calculatedcost!=null){
            setbudcost.setText("£"+calculatedcost);
        }

        back1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.main_menu);
                    MenuListen();
                    retrieveData();
                }
        });
        savebudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myBudget = mysetbudget.getText().toString();
                if (!myBudget.equals("")) {
                    Double dblBudget = Double.valueOf(myBudget);
                    Double calcTempCost = Double.valueOf(calculatedcost);
                    Date currentTime = Calendar.getInstance().getTime();
                    if (isNumeric(myBudget)) {
                        if (dblBudget >= calcTempCost) {
                            dbref.child("setBudget").setValue(dblBudget);
                            dbref.child("remainingBudget").setValue(dblBudget);
                            dbref.child("dateofBudgetSet").setValue(currentTime);

                            Toast.makeText(MainActivity.this, "Your budget was successfully updated to £" + dblBudget + ".", Toast.LENGTH_LONG).show();
                        } else {
                            mysetbudget.setError("Your budget must be higher than or equal to your weekly costs.");
                        }

                    } else {
                        mysetbudget.setError("Data entered is not numeric.");
                    }
                }
                else{
                    mysetbudget.setError("You should not leave this value blank.");
                }
            }

        });

    }
    public void CostCalculatorListen(){
        back2 = findViewById(R.id.btnBack2);
        calculatebtn = findViewById(R.id.btnCostSubmit);
        costbudget = findViewById(R.id.etBudgetSet);
        calc = findViewById(R.id.btnCalc);

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.main_menu);
                MenuListen();
                retrieveData();
            }
        });
        calc.setOnClickListener(new View.OnClickListener() {
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

                if (isNumeric(etArray[0]) && isNumeric(etArray[1]) && isNumeric(etArray[2]) && isNumeric(etArray[3])) {
                    for (int i = 0; i < 4; i++) {
                        String temp = etArray[i];
                        double convTemp = Double.valueOf(temp);
                        totalweeklycost = totalweeklycost + convTemp;
                    }
                    costvalue.setText("£" + totalweeklycost);

                }
                else{
                    Toast.makeText(MainActivity.this, "Values are not numeric.", Toast.LENGTH_LONG).show();
                }
            }});

        calculatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Double calcBudget;
                    String theBudget = costbudget.getText().toString();
                    if(isNumeric(theBudget)&&!theBudget.equals("")) {
                        calcBudget = Double.valueOf(theBudget);

                        if(calcBudget >= totalweeklycost){
                            Calendar cal = Calendar.getInstance();
                            Date currentTime = cal.getTime();
                            cal.add(Calendar.DATE, -1);
                            Date dateBefore1Day = cal.getTime();
                            userbudget.setDateofBudgetSet(currentTime);
                            userbudget.setDateofLastSubmission(dateBefore1Day);
                            userbudget.setSetBudget(calcBudget);
                            userbudget.setRemainingBudget(calcBudget);
                            userbudget.setCalculatedCost(totalweeklycost);
                            dbref.setValue(userbudget);
                            totalweeklycost=0.0;
                            Toast.makeText(MainActivity.this, "Your budget was successfully set to £" + calcBudget + ".", Toast.LENGTH_LONG).show();
                        }
                        else{
                            costbudget.setError("Your budget must be higher or equal to the weekly cost.");
                        }
                    }
                    else{
                        costbudget.setError("Your budget value is not numeric.");
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
        budgetvalue = findViewById(R.id.lbl_budget_val);
        budgetremaining = findViewById(R.id.lbl_allowance_val);


        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    budget = dataSnapshot.child("setBudget").getValue().toString();
                    budgetremainder = dataSnapshot.child("remainingBudget").getValue().toString();
                    calculatedcost = dataSnapshot.child("calculatedCost").getValue().toString();
                    datebudget = dataSnapshot.child("dateofBudgetSet").child("time").getValue().toString();
                    dateexp = dataSnapshot.child("dateofLastSubmission").child("time").getValue().toString();
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    Date datesub = new Date(Long.parseLong(dateexp));
                    dateexp= sf.format(datesub);
                    budgetvalue.setText("£"+budget);
                    budgetremaining.setText("£"+budgetremainder);
//
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error Receiving Data.", Toast.LENGTH_LONG).show();
            }
        });
    }

}


