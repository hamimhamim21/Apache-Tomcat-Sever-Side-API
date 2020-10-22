import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Grocery {

    public static final int MINIMUM_QUANTITY = 1;

    //TODO 01: add Min annotation? Does it have any params?
	//Yes it does
	@Min(value = 0,inclusive = false)
    private int id;

    //TODO 02: add NotNull annotation? Does it have any params?
	//No it doesn't
	@NotNull
    private Item item;

    //TODO 03: add NotNull annotation? Does it have any params?
	//	//No it doesn't
	@NotNull
    private LocalDate date;
    
    //TODO 04: add Min annotation? Does it have any params?
	@Min(value = MINIMUM_QUANTITY)
    private int quantity;

    //TODO 05: add NotNull annotation? Does it have any params?
	//No it doesn't
	@NotNull
    private FridgeDSC.SECTION section;

    // constructor
    public Grocery(int id, Item item, LocalDate date, int quantity, FridgeDSC.SECTION section) {
        this.id = id;
        this.item = item;
        this.date = date != null ? date : LocalDate.now();
        this.quantity = quantity;
        this.section = section;
    }

    // constructor
    public Grocery(Item item, LocalDate date, int quantity, FridgeDSC.SECTION section) throws Exception {
        this(0, item, date, quantity, section);
    }

    public Grocery(Item item, int quantity, FridgeDSC.SECTION section) throws Exception {
        this(item, null, quantity, section);
    }

    public int getId() {
        return this.id;
    }

    public Item getItem() {
        return this.item;
    }

    public String getItemName() {
        return this.item.getName();
    }

    public LocalDate getDate() {
        return this.date;
    }

    public String getDateStr() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FridgeDSC.DATE_FORMAT);

        return this.date.format(dtf);
    }

    public String getDaysAgo() {
        return FridgeDSC.calcDaysAgoStr(date);
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void updateQuantity() {
        this.quantity--;
    }

    public FridgeDSC.SECTION getSection() {
        return this.section;
    }
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(FridgeDSC.DATE_FORMAT);
        String daysAgo = FridgeDSC.calcDaysAgoStr(date);

        String itemStr = null;
        if (this.item != null)
            itemStr = this.item.getName() + (item.canExpire() ? " (EXP)":"");

        return "[ id: " + this.id
            + ", item: " + itemStr 
            + ", date: " + this.date.format(dtf) + " (" + daysAgo + ")"
            + ", quantity: " + this.quantity
            + ", section: " + this.section
            + " ]";
    }

    // To perform some quick tests
    public static void main(String [] args) throws Exception {
        // CONSIDER testing your validation annotations here
        // this is an example of a valid case; test each annotation accordingly, using invalid case(s)
        // NOTE: this is not a required task, but will help you test your Task 1 requirements
        Item i = new Item("Beef", true);
        Grocery g = new Grocery(1,i,LocalDate.now(),0, FridgeDSC.SECTION.COOLING);
		System.out.println(g);

        try {
            Validator.validate(g);
        } catch (ValidationException ve) {
            ve.printStackTrace();
        }
    }   
}
