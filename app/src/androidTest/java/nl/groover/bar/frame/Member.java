package nl.groover.bar.frame;

/**
 * Created by Ronald Olsthoorn on 5/2/2017.
 */

public class Member {

    private String firstName;
    private String lastName;
    private String prefix;

    private int id;

    public Member(String fn, String pf, String ln, int id){

        this.id = id;
        firstName = fn;
        prefix = pf;
        lastName = ln;
    }

    public int getId(){

        return id;
    }

    public String getFirstName(){

        return firstName;
    }

    public String getPrefix(){

        return prefix;
    }

    public  String getLastName(){

        return lastName;
    }

    public boolean equals(Object obj){

        if (obj == null) {
            return false;
        }
        if (!Member.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Member other = (Member) obj;

        if(other.id != this.id){
            return false;
        }else if(other.firstName.equals(this.firstName)){
            return false;
        }else if(other.prefix.equals(this.prefix)){
            return false;
        }else if(other.lastName.equals(this.lastName)){
            return false;
        }
        return true;

    }
}
