package com.sushi.foodordering.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.sushi.foodordering.EditProfileActivity;
import com.sushi.foodordering.HotDealActvity;
import com.sushi.foodordering.R;
import com.sushi.foodordering.entities.LoginObject;
import com.sushi.foodordering.util.CustomApplication;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private SharedPreferences prefs;
    private GoogleSignInClient googleSignInClient;
    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(getString(R.string.my_profile));


        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView profileImage = (ImageView) view.findViewById(R.id.profile_image);
        TextView profileName = (TextView) view.findViewById(R.id.profile_name);
        TextView profileAddress = (TextView) view.findViewById(R.id.profile_address);
        TextView profilePhone = (TextView) view.findViewById(R.id.profile_phone_number);
        TextView profileEmail = (TextView) view.findViewById(R.id.email);






        LoginObject loginUser = ((CustomApplication) getActivity().getApplication()).getLoginUser();
        profileName.setText(loginUser.getUsername());
        profileAddress.setText(loginUser.getAddress());
        profilePhone.setText(loginUser.getPhone());


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            profileName.setText(personName);
            profileEmail.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(profileImage);
        }


        Button editProfile = (Button) view.findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(editIntent);
            }
        });

        Button editCart = (Button) view.findViewById(R.id.edit_cart);
        editCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hotDealIntent = new Intent(getActivity(), HotDealActvity.class);
                startActivity(hotDealIntent);
            }
        });

        return view;



    }


}