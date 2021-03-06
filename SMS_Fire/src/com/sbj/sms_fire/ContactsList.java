package com.sbj.sms_fire;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.sbj.sms_fire.adapter.ContactsAdapter;
import com.sbj.sms_fire.model.ContactModel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsList extends Activity {

	private ContactsAdapter adapter;
	private ArrayList<ContactModel> arrListContactModel;
	private StringBuilder contactList;
	private EditText edContacts;
	private List<String> lstSelectedContacts;
	private ListView lstView;
	private TextWatcher onSearchTextChange = new TextWatcher() {
		public void afterTextChanged(Editable paramAnonymousEditable) {
		}

		public void beforeTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
		}

		public void onTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
			ContactsList.this.adapter = new ContactsAdapter(
					ContactsList.this,
					ContactsList.this
							.filterText(paramAnonymousCharSequence));
			ContactsList.this.lstView
					.setAdapter(ContactsList.this.adapter);
		}
	};

	private String selctedContacts;
	private StringBuilder usersContactList;

	private ArrayList<ContactModel> filterText(CharSequence paramCharSequence) {
		ArrayList localArrayList = new ArrayList();
		for (int i = 0;; i++) {
			if (i >= this.arrListContactModel.size())
				return localArrayList;
			ContactModel localContactModel = (ContactModel) this.arrListContactModel
					.get(i);
			if (localContactModel
					.getContactName()
					.trim()
					.toLowerCase()
					.contains(paramCharSequence.toString().trim().toLowerCase()))
				localArrayList.add(localContactModel);
		}
	}

	private Cursor getContacts() {
		return managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { "_id", "display_name", "data1" }, null,
				(String[]) null, "display_name COLLATE LOCALIZED ASC");

	}

	private void initializeComponents() {

		this.edContacts = ((EditText) findViewById(R.id.locateme_contacts_edContacts));
		this.lstView = ((ListView) findViewById(R.id.locateme_contacts_lstView));
		this.arrListContactModel = new ArrayList();
		this.selctedContacts = getIntent().getStringExtra("SELECTED_ID");
		this.usersContactList = new StringBuilder();
		String[] arrayOfString = this.selctedContacts.split(",");
		this.lstSelectedContacts = new ArrayList();
		int len = arrayOfString.length;
		for (int i = 0; i < len; i++) {
			StringTokenizer localStringTokenizer;
			if ((arrayOfString[i].contains("<"))
					&& (arrayOfString[i].contains(">"))) {
				localStringTokenizer = new StringTokenizer(arrayOfString[i],
						"<>");
				if (localStringTokenizer.countTokens() == 2) {
					localStringTokenizer.nextToken();
					String str1 = localStringTokenizer.nextToken();
					this.lstSelectedContacts.add(str1);
				}
			}
		}

		Cursor localCursor;

		this.edContacts.addTextChangedListener(this.onSearchTextChange);

		localCursor = getContacts();
		Log.e("", "Count :" + localCursor.getCount());
		localCursor.moveToFirst();

		do {

			ContactModel localContactModel = new ContactModel();
			localContactModel.setContactID(localCursor.getString(localCursor
					.getColumnIndex("_id")));
			localContactModel.setContactName(localCursor.getString(localCursor
					.getColumnIndex("display_name")));
			String str2 = localCursor.getString(localCursor
					.getColumnIndex("data1"));
			localContactModel.setContactNumber(str2);

			if (this.lstSelectedContacts.contains(str2))
				localContactModel.setSelectFlag(true);
			else
				localContactModel.setSelectFlag(false);

			this.arrListContactModel.add(localContactModel);

			if (localCursor.isLast()) {
				localCursor.close();
				this.adapter = new ContactsAdapter(this,
						this.arrListContactModel);
				this.lstView.setAdapter(this.adapter);
				break;
			}

		} while (localCursor.moveToNext());
	}

	public void onButtonClick(View paramView) {
		((InputMethodManager) getSystemService("input_method"))
				.hideSoftInputFromWindow(this.edContacts.getWindowToken(), 0);
		this.contactList = new StringBuilder();
		for (int i = 0;; i++) {
			if (i >= this.arrListContactModel.size()) {
				Intent localIntent = new Intent();
				localIntent.putExtra(
						"CONTACT_DATA",
						this.contactList.toString()
								+ this.usersContactList.toString());
				setResult(-1, localIntent);
				finish();
				return;
			}
			ContactModel localContactModel = (ContactModel) this.arrListContactModel
					.get(i);
			if (localContactModel.isSelectFlag())
				this.contactList.append(localContactModel.getContactName()
						+ "<" + localContactModel.getContactNumber() + ">,");
		}
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.contacts_activity);
		initializeComponents();
		this.edContacts
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(
							TextView paramAnonymousTextView,
							int paramAnonymousInt,
							KeyEvent paramAnonymousKeyEvent) {
						if (paramAnonymousInt == 6)
							((InputMethodManager) ContactsList.this
									.getSystemService("input_method"))
									.hideSoftInputFromWindow(
											ContactsList.this.edContacts
													.getWindowToken(), 0);
						return false;
					}
				});
	}
}
