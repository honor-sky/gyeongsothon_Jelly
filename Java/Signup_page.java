package org.gyeongsoton.gyeongsoton_jelly;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class Signup_page extends AppCompatActivity {

    private EditText username, ID, password,editTextPhone;
    private Button join_button, check_button;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        //아이디값 찾아주기
        username = findViewById( R.id.username );
        ID = findViewById( R.id.ID );
        password = findViewById( R.id.password );
        editTextPhone = findViewById(R.id.editTextPhone);


        //아이디 중복 체크
        check_button = findViewById(R.id.check_button);
        check_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String UserID = ID.getText().toString();
                if (validate) {
                    return; //검증 완료
                }

                if (UserID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup_page.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup_page.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                ID.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                check_button.setBackgroundColor(getResources().getColor(R.color.white));
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Signup_page.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(UserID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Signup_page.this);
                queue.add(validateRequest);
            }
        });

        //회원가입 버튼 클릭 시 수행
        join_button = findViewById( R.id.join_button);
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UserName = username.getText().toString();
                final String UserID = ID.getText().toString();
                final String UserPass = password.getText().toString();
                final String UserNum = editTextPhone.getText().toString();


                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup_page.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우
                if (UserName.equals("") || UserID.equals("") || UserPass.equals("")||UserNum.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Signup_page.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            //회원가입 성공시
                            if (success) {

                                Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserName), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Signup_page.this, Login_page.class);
                                startActivity(intent);

                                //회원가입 실패시
                            } else {
                                Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //서버로 Volley를 이용해서 요청
                SignupRequest signupRequest = new SignupRequest( UserID, UserPass, UserName, UserNum,responseListener);
                RequestQueue queue = Volley.newRequestQueue( Signup_page.this );
                queue.add( signupRequest );
            }
        });

    }

}


