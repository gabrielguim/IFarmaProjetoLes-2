package tests;

import org.junit.*;

import exceptions.InvalidAddressException;
import exceptions.InvalidCEPException;
import exceptions.InvalidCNPJException;
import exceptions.InvalidCPFException;
import exceptions.InvalidEmailException;
import exceptions.InvalidHouseNumberException;
import exceptions.InvalidNameException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidUserDataException;
import model.Customer;
import model.Pharma;
import model.AbstractUser;

public class PharmaDataValidationTests {
			
	private String validCNPJ;
	private String invalidCNPJ;

	private Pharma pharma;
	private Pharma pharmaCrt;
			
	@Before
	public void setUp() throws InvalidUserDataException{				
		pharma = new Pharma();
		validCNPJ = "46633676000113";
		invalidCNPJ = "00000670000003";
		
	}
		
	@Test
	public void testShouldNotSetCNPJOfPharma(){
		try {
			pharma.setCnpj(invalidCNPJ);
		} catch (InvalidCNPJException e) {
			Assert.assertEquals(new InvalidCNPJException().getMessage(), e.getMessage());
		}	
	}
	
	@Test
	public void testShouldSetCNPJOfPharma(){
		try {
			pharma.setCnpj(validCNPJ);
		} catch (InvalidCNPJException e) {
			Assert.assertEquals(new InvalidCNPJException().getMessage(), e.getMessage());
		}
		Assert.assertEquals(validCNPJ, pharma.getCnpj());
	}
	
	@Test
	public void testShouldFailCreatingInvalidName(){
		try {
			pharmaCrt = new Pharma("Gilo boy ph@rm@", "redepharma@hotmail.com", 
					"5151526w2a", "Rua das Cebolas que n�o fazem chorar", 
					"1500", "58410538");
		} catch (InvalidUserDataException e) {
			Assert.assertEquals(new InvalidNameException().getMessage(), e.getMessage());
		}
	}

}