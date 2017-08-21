package me.aflak.filterlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.aflak.libraries.SpecFilter;
import me.aflak.libraries.UserFilter;
import me.aflak.utils.Operation;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<User> users = getUsers();

        UserFilter.builder()
                .body().matches(SpecFilter.builder()
                    .size().greaterThan(175)
                    .build(), SpecFilter.class)
                .firstName().regex(Pattern.compile("^[A-Z].*"))
                .postOperation(new Operation<User>() {
                    @Override
                    public void execute(User object) {
                        Log.d(TAG, object.getFirstName()+" "+object.getLastName());
                    }
                })
                .on(users);
    }

    public List<User> getUsers(){
        List<User> users = new ArrayList<>();
        users.add(new User(20, false, "Arya", "Stark", "Bristol", 15, "April", 1997, new Spec(150, 70)));
        users.add(new User(30, true, "Jon", "Snow", "Acton", 26, "December", 1986, new Spec(150, 70)));
        users.add(new User(48, true, "tyrion", "Lannister", "Morristown", 11, "June", 1969, new Spec(180, 70)));
        users.add(new User(21, false, "Sansa", "Stark", "Northampton", 21, "February", 1996, new Spec(150, 70)));
        users.add(new User(30, false, "Daenerys", "Targaryen", "London", 23, "October", 1986, new Spec(150, 70)));
        users.add(new User(43, false, "Cersei", "Lannister", "Hamilton", 3, "October", 1973, new Spec(150, 70)));
        users.add(new User(47, true, "Jaime", "Lannister", "Rudkøbing", 27, "July", 1970, new Spec(180, 70)));
        return users;
    }
}
