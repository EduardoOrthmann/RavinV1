package address;

public class Address {
    private int id;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String neighborhood;
    private String street;

    public Address(int id, String country, String state, String city, String zipCode, String neighborhood, String street) {
        this.id = id;
        this.country = country;
        this.state = state;
        this.city = city;
        this.zipCode = zipCode;
        this.neighborhood = neighborhood;
        this.street = street;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}
