package rs.ac.uns.ftn.informatika.jpa.domain;

import java.io.Serializable;
import java.util.List;

//-----JSON START--------------------------------------------------
/*
 * Objekat koji ce se u bazu upisati kao JSON
 */
public class ContactInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<String> phoneNumbers;
	private List<String> addresses;
	private List<String> emailAdresses;
	
	public ContactInfo() {
		super();
	}

	public ContactInfo(List<String> phoneNumbers, List<String> addresses, List<String> emailAdresses) {
		super();
		this.phoneNumbers = phoneNumbers;
		this.addresses = addresses;
		this.emailAdresses = emailAdresses;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}

	public List<String> getEmailAdresses() {
		return emailAdresses;
	}

	public void setEmailAdresses(List<String> emailAdresses) {
		this.emailAdresses = emailAdresses;
	}

	@Override
	public String toString() {
		return "ContactInfo [phoneNumbers=" + phoneNumbers + ", addresses=" + addresses + ", emailAdresses="
				+ emailAdresses + "]";
	}
	//-----JSON END--------------------------------------------------

}
