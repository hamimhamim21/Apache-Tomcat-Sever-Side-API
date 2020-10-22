import java.util.List;

public class GroceryController {
    private FridgeDSC fridgeDSC;
    public GroceryController(String dbHost, String dbUserName, String dbPassword) throws Exception {
        fridgeDSC = new FridgeDSC(dbHost, dbUserName, dbPassword);
        try {
            fridgeDSC.connect();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    public List<Grocery> get() throws Exception {
        // Note: In our design this should match a http GET /fridge/api/grocery . It returns a list of all the groceries in the grocery table. 
        // TODO 06: HINT: a relevant call to a fridgeDSC method. 
        return fridgeDSC.getAllGroceries(); // the completed method should not return null
    }
    public Grocery get(int id) throws Exception {
        // Note: In our design this should match a http GET /fridge/api/grocery/<id> . It returns the grocery in the grocery table that matches the id.
        // TODO 07: HINT: a relevant call to a fridgeDSC method
        return fridgeDSC.searchGrocery(id); // the completed method should not return null
    }
    public List<Grocery> getAllExpiredItems() throws Exception {
        // Note: In our design this should match a http GET /fridge/api/grocery/e . It returns the groceries in the grocery table that have items with the expired Boolean set.
        // TODO 08: HINT: a relevant call to a fridgeDSC method
        return fridgeDSC.getAllGroceryExpiredItems(); // the completed method should not return null
    }
    public int add(Grocery g) throws Exception {
        // Note: in our design this should match a http POST <grocery data>. It returns the id of the newly created grocery
        // TODO 09: validate argument g, using Validation Framework
		 Validator.validate(g);
		 String name = g.getItemName();
		 
		 int quantity = g.getQuantity();
		 
		 FridgeDSC.SECTION section = g.getSection();
        // TODO 10: make a relevant call to a fridgeDSC method
		int new_id = fridgeDSC.addGrocery(name, quantity,section);
		
        return new_id;// this method should return the id of the newly created grocery
    }
	
    public Grocery update(int id) throws Exception {
        // Note: In our design this should match a http PUT <id>.  It returns the updated grocery in the grocery table that matches the id.  
        // TODO 11: make a relevant call to a fridgeDSC method
		
        return fridgeDSC.useGrocery(id);// this method should return the updated grocery object
    }
    public int delete(int id) throws Exception {
        // Note: In our design this should match a http DELETEE <id>. It returns the number of rows effected in the grocery table.  
        // TODO 12: make a relevant call to a fridgeDSC method
        return fridgeDSC.removeGrocery(id); // this method should return what ever the fridgeDSC method call (TODO 12) returns
    }
    // To perform some quick tests
    /*public static void main(String [] args) throws Exception {
        System.out.println("GroceryController main...");
        // CONSIDER testing each of the above methods here
        // NOTE: this is not a required task, but will help you test your Task 2 requirements
        try {
            // CHANGE the username and password to match yours
            // CHANGE the first param to your database host if you need to
            GroceryController gc = new GroceryController("localhost:3306/fridgedb", "root","admin");
            System.out.println(gc.get()); // get all groceries
            System.out.println(gc.get(19)); // some id that exists in your grocery table
            Item myitem = new Item("Beef",true);
            System.out.println(gc.add(new Grocery(myitem,5,FridgeDSC.SECTION.MEAT)));
            System.out.println(gc.update(36)); // some id that exists in your grocery table
            System.out.println(gc.delete(36)); // some id that exists in your grocery table
            // add others as you wish
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        
    }*/
}
