package model;

public class Publisher {
    private int publisherId;
    private String publisherName;
    private String address;
    private String contact;

    public Publisher() {}

    public Publisher(int publisherId, String publisherName, String address, String contact) {
        this.publisherId = publisherId;
        this.publisherName = publisherName;
        this.address = address;
        this.contact = contact;
    }

    public int getPublisherId() { return publisherId; }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
}
