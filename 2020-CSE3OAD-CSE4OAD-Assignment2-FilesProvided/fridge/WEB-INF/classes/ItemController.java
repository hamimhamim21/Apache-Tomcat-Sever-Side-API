import java.util.List;

public class ItemController {

    private FridgeDSC fridgeDSC;

    public ItemController(String dbHost, String dbUserName, String dbPassword) throws Exception {
        fridgeDSC = new FridgeDSC(dbHost, dbUserName, dbPassword);

        try {
            fridgeDSC.connect();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public List<Item> get() throws Exception {
        //TODO 13: HINT: a relevant call to a fridgeDSC method
        return fridgeDSC.getAllItems(); // this method should not return null
    }
    
    public List<Item> getAllExpiredItems() throws Exception {
        return fridgeDSC.getAllExpiredItems();
    }

    // To perform some quick tests
    /*public static void main(String [] args) throws Exception {
        // CONSIDER testing each of the above methods here
        // NOTE: this is not a required task, but will help you test your Task 2 requirements
        try {
            // CHANGE the username and password to match yours
            // CHANGE the first param to your database host if you are not using latcs7
            ItemController ic = new ItemController("localhost:3306/fridgedb", "root", "admin");
             UNCOMMENT the following and add the relevant parameters/arguments to do your testing
            System.out.println(ic.get());


        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }*/
}
