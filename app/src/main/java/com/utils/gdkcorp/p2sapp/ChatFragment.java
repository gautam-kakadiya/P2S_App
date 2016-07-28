package com.utils.gdkcorp.p2sapp;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String HI = "Hi";
    private RecyclerView chat_rview;
    private ChatRecyclerViewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ImageButton send_button;
    private EditText edit_chat_msg;
    private ArrayList<ChatMessage> list = new ArrayList<ChatMessage>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private StringBuilder msg;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        send_button = (ImageButton) v.findViewById(R.id.send_button);
        edit_chat_msg = (EditText) v.findViewById(R.id.edit_msg);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chat_rview = (RecyclerView) view.findViewById(R.id.chat_recyclerview);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new ChatRecyclerViewAdapter(getActivity(), list);
        chat_rview.setLayoutManager(layoutManager);
        chat_rview.setAdapter(adapter);

        send_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (edit_chat_msg.getText().toString().equalsIgnoreCase("")) {
            edit_chat_msg.setError("Enter some text first");
        } else {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMsg(edit_chat_msg.getText().toString());
            chatMessage.setIsMe(true);
            adapter.addMsg(chatMessage);
            edit_chat_msg.setText("");
            chat_rview.scrollToPosition(adapter.getItemCount() - 1);
            View view1 = getActivity().getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            responce(chatMessage.getMsg());

        }

    }

    private void responce(String msg) {
        if (msg.toLowerCase().equalsIgnoreCase("hi")) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ChatMessage chat_msg = new ChatMessage();
                    chat_msg.setMsg("Typing...");
                    chat_msg.setIsMe(false);
                    adapter.addMsg(chat_msg);
                    chat_rview.scrollToPosition(adapter.getItemCount() - 1);
                }
            }, 1000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ChatMessage chtmsg = new ChatMessage();
                    chtmsg.setMsg("Hello sir,How may I help you?");
                    chtmsg.setIsMe(false);
                    adapter.refreshlastmsg(chtmsg);
                    chat_rview.scrollToPosition(adapter.getItemCount() - 1);
                }
            }, 3000);
        } else {
            new MyAsyncTask1().execute(msg);
        }
    }

    private String[] tokenize(String msg) throws IOException {
        InputStream modelIn = getActivity().getAssets().open("en-token.bin");
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);
        String tokens[] = tokenizer.tokenize(msg);
        return tokens;
    }

    public class MyAsyncTask1 extends AsyncTask<String, ChatMessage, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... strings) {
            InputStream modelIn;
            try {
                String tokens[] = tokenize(strings[0]);
                modelIn = getActivity().getAssets().open("en-ner-product.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                  Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                ChatMessage chatmsg = new ChatMessage();
                chatmsg.setIsMe(false);
                msg = new StringBuilder("");
                for (int i = 0; i < span.length; ++i) {
                    for (int j = span[i].getStart(); j < span[i].getEnd(); ++j) {
                        msg.append(tokens[j] + " ");
                    }
                    msg.append(" | ");
                }
                Log.d("current_data", "doInBackground: " + msg.toString());
                chatmsg.setMsg(msg.toString());
                publishProgress(chatmsg);
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ChatMessage... values) {
            adapter.addMsg(values[0]);
            chat_rview.scrollToPosition(adapter.getItemCount() - 1);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}
