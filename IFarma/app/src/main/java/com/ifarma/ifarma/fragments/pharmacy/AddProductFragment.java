package com.ifarma.ifarma.fragments.pharmacy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ifarma.ifarma.R;
import com.ifarma.ifarma.adapters.MedicineSearchAdapter;
import com.ifarma.ifarma.controllers.FirebaseController;
import com.ifarma.ifarma.controllers.OnCurrentPharmaGetDataListener;
import com.ifarma.ifarma.controllers.OnMedGetDataListener;
import com.ifarma.ifarma.exceptions.InvalidProductDataException;
import com.ifarma.ifarma.fragments.user.SearchFragment;
import com.ifarma.ifarma.model.Pharma;
import com.ifarma.ifarma.model.Product;
import com.ifarma.ifarma.services.AdapterService;
import com.ifarma.ifarma.util.Validate;

import java.util.ArrayList;
import java.util.List;

public class AddProductFragment extends Fragment {

    private View rootView;
    private AppCompatButton _cadastrar;
    private EditText _nameProductInput;
    private EditText _priceProductInput;
    private EditText _labProductInput;
    private EditText _descriptionProductInput;
    private CheckBox _genericoCheckbox;
    private ImageView _backButton;
    public static final String FLAG_EMAIL = "currentEmail";
    private MedicineSearchAdapter adapterMed;
    private String pharmacyName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_addproduct, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defaultState = "";
        String email =  prefs.getString(FLAG_EMAIL, defaultState);
        email = email.replace(".", "dot");
        configPharmacyName(email);

        _cadastrar = (AppCompatButton) rootView.findViewById(R.id.btn_cadastrar);
        _nameProductInput = (EditText) rootView.findViewById(R.id.input_name_product);
        _priceProductInput = (EditText) rootView.findViewById(R.id.input_price_product);
        _labProductInput = (EditText) rootView.findViewById(R.id.input_lab_product);
        _descriptionProductInput = (EditText) rootView.findViewById(R.id.input_description_product);
        _genericoCheckbox = (CheckBox) rootView.findViewById(R.id.checkbox_generico);

        _backButton = (ImageView) rootView.findViewById(R.id.back_btn_cadastro_product);

        final FrameLayout _frameLayout = (FrameLayout) getActivity().findViewById(R.id.fragment_container);
        _frameLayout.setVisibility(View.VISIBLE);

        _backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout _pagerLayout = (LinearLayout) getActivity().findViewById(R.id.layout_pager);
                _pagerLayout.setVisibility(View.VISIBLE);
                _frameLayout.setVisibility(View.GONE);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new MedicinesFragment());
                fragmentTransaction.commit();
            }
        });

        _cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateLogin(_nameProductInput, _priceProductInput, _labProductInput, _descriptionProductInput)){
                    Toast.makeText(getContext(), "Falha ao criar o produto!", Toast.LENGTH_SHORT).show();
                } else {
                    Product product = new Product();
                    String name = _nameProductInput.getText().toString();
                    Double price = Double.parseDouble(_priceProductInput.getText().toString());
                    String lab = _labProductInput.getText().toString();
                    String description = _descriptionProductInput.getText().toString();
                    boolean generic = _genericoCheckbox.isChecked();
                    try {
                        product.setNameProduct(name);
                        product.setPrice(price);
                        product.setLab(lab);
                        product.setDescription(description);
                        product.setGeneric(generic);
                        product.setPharmacyName(pharmacyName);
                    } catch (InvalidProductDataException e) {
                        e.printStackTrace();
                    }

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String defaultState = "";
                    String email =  prefs.getString(FLAG_EMAIL, defaultState);
                    product.setPharmacyId(email);
                    email = email.replace(".", "dot");
                    FirebaseController.newProduct(email, product);

                    LinearLayout _pagerLayout = (LinearLayout) getActivity().findViewById(R.id.layout_pager);
                    _pagerLayout.setVisibility(View.VISIBLE);
                    _frameLayout.setVisibility(View.GONE);

                    Toast.makeText(getContext(), "Produto criado com sucesso!", Toast.LENGTH_SHORT).show();
                    AdapterService.reloadAdapter(0);

                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, new MedicinesFragment());
                    fragmentTransaction.commit();
                }
            }
        });

        return rootView;
    }

    public void configPharmacyName(String email){

        FirebaseController.retrieveCurrentPharma(email, new OnCurrentPharmaGetDataListener() {

            @Override
            public void onStart() {}

            @Override
            public void onSuccess(Pharma currentPharma) {
                pharmacyName = currentPharma.getName();
                return;
            }
        });

    }

    private static boolean validateLogin(EditText _nameProductInput, EditText _priceProductInput,
                                         EditText _labProductInput, EditText _descriptionProductInput){

        boolean isValid = Validate.isValidMedicine(_nameProductInput, _priceProductInput, _labProductInput, _descriptionProductInput);

        return isValid;
    }
}
