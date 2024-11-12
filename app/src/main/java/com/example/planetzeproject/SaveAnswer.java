import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class SaveAnswer{
    public void saveAnswer(Firebase db, String user, int qnum, String answer){ //Pass db as arg so we don't need to set it every time
        DatabaseReference qref = db.getReference("Users");
        qref.child(user).child(qnum).setValue(answer);
    }
    /* Alternative saveAnswer function if we store the answers in a User class
    public void saveAnswer(Firebase db, User user){
        DatabaseReference qref = db.getReference("Users");
        qref.child(user).setValue(user); //Should have user w/ ArrayList of answers     
    }
    /*
}