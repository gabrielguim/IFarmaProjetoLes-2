package com.ifarma.ifarma.controllers;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.ifarma.ifarma.exceptions.InvalidNameException;
import com.ifarma.ifarma.exceptions.InvalidProductDataException;
import com.ifarma.ifarma.exceptions.InvalidUserDataException;
import com.ifarma.ifarma.model.Customer;
import com.ifarma.ifarma.model.Pharma;
import com.ifarma.ifarma.util.Utils;
import com.ifarma.ifarma.model.Product;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mafra on 17/02/2017.
 */

public class FirebaseController {

    private final static String CUSTOMERS = "customers";
    private final static String PHARMACIES = "pharmacies";
    private final static String PRODUCTS = "products";
    public static final String FIREBASE_URL = "https://ifarma-5d2e6.firebaseio.com/";

    private static Firebase firebase;

    public static Firebase getFirebase(){
        if( firebase == null ){
            firebase = new Firebase(FIREBASE_URL);
        }
        return firebase;
    }

    public static void saveCustomer(String name, String email, String address, String houseNumber, String cep,
                                    String cpf){

        Utils.showSavingCostumerMsg();

        Firebase firebaseRef = getFirebase();
        Firebase customersReference = firebaseRef.child(CUSTOMERS);

        Customer customer = new Customer();
        try {
            customer.setName(name);
            customer.setCpf(cpf);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setHouseNumber(houseNumber);
            customer.setCep(cep);
        } catch (InvalidUserDataException e) {
            e.printStackTrace();
        }

        String customerNode = Utils.convertEmail(customer.getEmail());

        customersReference.child(customerNode).setValue(customer);
    }

    public static void editCustomer(Customer customer) {

        Firebase firebaseRef = getFirebase();
        Firebase customersReference = firebaseRef.child(CUSTOMERS);

        String emailNode = Utils.convertEmail(customer.getEmail());

        customersReference.child(emailNode).setValue(customer);

    }

    public static void savePharmacy(String name, String email, String address, String houseNumber, String cep,
                                    String cnpj) {

        Firebase firebaseRef = getFirebase();
        Firebase pharmarciesReference = firebaseRef.child(PHARMACIES);

        Pharma pharma = new Pharma();
        try {
            pharma.setCep(cep);
            pharma.setHouseNumber(houseNumber);
            pharma.setAddress(address);
            pharma.setName(name);
            pharma.setEmail(email);
            pharma.setCnpj(cnpj);
        } catch (InvalidUserDataException e) {
            e.printStackTrace();
        }

        pharma.initProducts();

        String emailNode = Utils.convertEmail(pharma.getEmail());

        pharmarciesReference.child(emailNode).setValue(pharma);

    }

    public static void editPharmacy(Pharma pharma) {

        Firebase firebaseRef = getFirebase();
        Firebase pharmarciesReference = firebaseRef.child(PHARMACIES);

        String emailNode = Utils.convertEmail(pharma.getEmail());

        pharmarciesReference.child(emailNode).setValue(pharma);

    }

    public static void retrievePharmacies(final OnPharmaGetDataListener listener){
        final Firebase pharmasReference = getFirebase().child(PHARMACIES);
        final List<Pharma> lista = new ArrayList<>();

        pharmasReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Pharma pharma = dataSnapshot.getValue(Pharma.class);
                lista.add(pharma);

                listener.onSuccess(lista);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Utils.showChildModifiedMsg();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Utils.showChildRemovedMsg();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                Utils.showChildMovedMsg();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Utils.showOnCancelledMsg();
            }


        });
    }

    public static void retrieveProducts( final OnMedGetDataListener listener) {
        final Firebase productsReference = getFirebase().child(PHARMACIES);
        final List<Product> lista = new ArrayList<>();

        productsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                DataSnapshot productSnapshot = dataSnapshot.child(PRODUCTS);
                Iterable<DataSnapshot> productChildren = productSnapshot.getChildren();

                for (DataSnapshot prod : productChildren){
                    Product product = prod.getValue(Product.class);
                    lista.add(product);
                }

                listener.onSuccess(lista);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                Utils.showChildModifiedMsg();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Utils.showChildRemovedMsg();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                Utils.showChildMovedMsg();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Utils.showOnCancelledMsg();
            }


        });
    }

    private void collectPhoneNumbers(Map<String,Object> users) {

        ArrayList<Long> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            System.out.println(entry.toString());
        }

        System.out.println(phoneNumbers.toString());
    }

    public static void newProduct(String pharmacyId, Product product){

        Firebase firebaseRef = getFirebase();
        Firebase productsPharmacyReference = firebaseRef.child(PHARMACIES).child(pharmacyId).child(PRODUCTS);
        Firebase newProduct = productsPharmacyReference.push();

        newProduct.setValue(product);
    }

    public static void saveProduct(String name, double price, String lab, String description, boolean generic){

        Firebase firebaseRef = getFirebase();
        Firebase productsReference = firebaseRef.child(PRODUCTS);

        Utils.showSavingProductMsg();

        Product product = new Product();
        try {
            product.setNameProduct(name);
            product.setPrice(price);
            product.setLab(lab);
            product.setDescription(description);
            product.setGeneric(generic);
        } catch (InvalidProductDataException e) {
            e.printStackTrace();
        }

        productsReference.child(product.getNameProduct()).setValue(product);

    }

    public static void retrieveCurrentPharma(String pharmaID, final OnCurrentPharmaGetDataListener listener) {
        final Firebase pharmasReference = getFirebase().child(PHARMACIES).child(pharmaID);

        pharmasReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pharma pharma = dataSnapshot.getValue(Pharma.class);
                listener.onSuccess(pharma);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public static void retrieveCurrentCustomer(String customerID, final OnCurrentCustomerGetDataListener listener) {
        final Firebase pharmasReference = getFirebase().child(CUSTOMERS).child(customerID);

        pharmasReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                listener.onSuccess(customer);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }



}
