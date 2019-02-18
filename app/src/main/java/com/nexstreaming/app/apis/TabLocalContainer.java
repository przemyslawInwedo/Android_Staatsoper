package com.nexstreaming.app.apis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.nunc.com.staatsoperlivestreaming.R;

import java.util.List;

public class TabLocalContainer extends Fragment {

	private boolean mIsViewInited;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_local_container, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("test", "tab 1 container on activity created");
		if (!mIsViewInited) {
			mIsViewInited = true;
			initView();
		}
	}

	protected void initView() {
		replaceFragment(new TabLocal(), false);
	}

	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.replace(R.id.fragment_container, fragment);
		transaction.commit();
		getChildFragmentManager().executePendingTransactions();
	}

	public boolean popFragmentOrFolder() {
		boolean isPop = false;
		if (getChildFragmentManager().getBackStackEntryCount() > 0) {
			isPop = true;
			getChildFragmentManager().popBackStack();
		} else {
			List<Fragment> fragmentList = getChildFragmentManager().getFragments();
			if( fragmentList != null && fragmentList.size() > 0 ) {
				Fragment fragment = fragmentList.get(0);
				if( fragment instanceof TabLocal) {
					TabLocal local = (TabLocal)fragment;
					isPop = local.popFolder();
				}
			}
		}
		return isPop;
	}
}
