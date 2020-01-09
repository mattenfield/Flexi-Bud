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

public class MainActivity extends AppCompatActivity {
//view widgets
    ProgressBar progressBar;
    EditText email;
    EditText password,confpassword;
    Button signin;
    Button signup;
    FirebaseAuth firebaseAuth;
    TextView allowancevalue;
    TextView budgetvalue;
    TextView overview;
    EditText todaysbudget;
    Button weeklycostcalculator;
    Button setbudget;
    Button logout;
    Button back1;
    Button back2;
    Button btnConfirm;

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
                firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>(){

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

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
        setbudget = findViewById(R.id.btnSetBudget);
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

        back1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setContentView(R.layout.main_menu);
                    MenuListen();
                }
        });

    }
    public void CostCalculatorListen(){
        back2 = findViewById(R.id.btnBack2);

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.main_menu);
                MenuListen();
            }
        });
    }
}


