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
    private StringBuilder msg = new StringBuilder("");
    private CurrentProduct cp = new CurrentProduct();
    private int ok_message_flag = 0;
    private int count = 0;
    private boolean dont_create_product = false;
    private int prod_exist_no = -1;
    private int current_prod_no = -1;
    ArrayList<DetectedProduct> prod_list = new ArrayList<DetectedProduct>();

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
        } else if (ok_message_flag != 0) {
            String message = edit_chat_msg.getText().toString();
            edit_chat_msg.setText("");
            hideKeyboard(view);
            generateChatMessage(message, true, false);
            generateTypingResponce();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    generateChatMessage("ok", false, true);
                }
            }, 2000);
            ok_message_flag = 0;
        } else {
            String message = edit_chat_msg.getText().toString();
            edit_chat_msg.setText("");
            hideKeyboard(view);
            generateChatMessage(message, true, false);
            generateTypingResponce();
            new TokenizeAsyncTask().execute(message);


           /* ChatMessage chatMessage = new ChatMessage();
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
            try {
                responce(chatMessage.getMsg());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        }

    }

    private void generateResponce(String message) {

    }

    private void generateTypingResponce() {
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
    }

    private void generateChatMessage(String s, boolean isMe, boolean replaceLast) {
        ChatMessage msg = new ChatMessage();
        msg.setMsg(s);
        msg.setIsMe(isMe);
        if (replaceLast) {
            adapter.refreshlastmsg(msg);
        } else {
            adapter.addMsg(msg);
        }
        chat_rview.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void hideKeyboard(View view) {
        View view1 = getActivity().getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void responce(String msg) throws IOException {
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
            new TokenizeAsyncTask().execute(msg);
            //ChatMessage chatMessage = cp.genrate_msg();
            //adapter.addMsg(chatMessage);
        }
    }

    private String[] tokenize(String msg) throws IOException {
        InputStream modelIn = getActivity().getAssets().open("en-token.bin");
        TokenizerModel model = new TokenizerModel(modelIn);
        Tokenizer tokenizer = new TokenizerME(model);
        String tokens[] = tokenizer.tokenize(msg);
        return tokens;
    }

    public class CategorizeTask extends AsyncTask<String[], String, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String[]... strings) {
            InputStream modelIn;
            try {
                String tokens[] = strings[0];
                modelIn = getActivity().getAssets().open("en-ner-category.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    publishProgress(span[i].getType());
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String category = values[0];
            Log.d("product", "catagory: " + category);
            //cp.categories.add(category);
            for (int i = 0; i < prod_list.size(); ++i) {
                DetectedProduct prod = prod_list.get(i);
                if (prod.getBrand().equalsIgnoreCase(category)) {
                    dont_create_product = true;
                    current_prod_no = i;
                    break;
                }
            }
            if (current_prod_no == -1) {
                DetectedProduct prod = new DetectedProduct();
                prod.setCategory(category);
                prod_list.add(prod);
                current_prod_no = prod_list.size() - 1;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    public class SubCategorizeTask extends AsyncTask<String[], String, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String[]... strings) {
            InputStream modelIn;
            try {
                String tokens[] = strings[0];
                modelIn = getActivity().getAssets().open("en-ner-sub-category1.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    publishProgress(span[i].getType());
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String subcategory = values[0];
            Log.d("product", "subcatagory: " + subcategory);
            //cp.sub_catagories.add(subcategory);

            if (current_prod_no != -1) {
                prod_list.get(current_prod_no).setSub_catagory(subcategory);
            } else {
                DetectedProduct prod = new DetectedProduct();
                prod.setSub_catagory(subcategory);
                prod_list.add(prod);
                current_prod_no = prod_list.size() - 1;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }

    public class BrandDetectionTask extends AsyncTask<String[], String, Void> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String[]... strings) {
            InputStream modelIn;
            try {
                String tokens[] = strings[0];
                modelIn = getActivity().getAssets().open("en-ner-brand.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: " + tokens.length);
                Span span[] = namefinder.find(tokens);
                for (int i = 0; i < span.length; ++i) {
                    publishProgress(span[i].getType());
                }

            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String brand = values[0];
            Log.d("product", "brand: " + brand);
            /*cp.brands.add(brand);*/

            if (current_prod_no != -1) {
                prod_list.get(current_prod_no).setBrand(brand);
            } else {
                DetectedProduct prod = new DetectedProduct();
                prod.setBrand(brand);
                prod_list.add(prod);
                current_prod_no = prod_list.size() - 1;
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Log.d("product", "cp :" + cp.categories.size() + ":" + cp.sub_catagories.size());
            //String chatMessage = cp.genrateProduct();
            //generateChatMessage(chatMessage,false,true);
            if (current_prod_no != -1) {
                DetectedProduct prod = prod_list.get(current_prod_no);
                if (prod.getSub_catagory() == null) {
                    generateChatMessage("Which structure of " + prod.getCategory() + " you want?", false, true);
                } else if (prod.getBrand() == null) {
                    generateChatMessage("Which brands " + prod.getCategory() + " " + prod.getSub_catagory() + " you want?", false, true);
                } else {
                    String prod_msg = prod.generateProduct();
                    generateChatMessage("Is this the product which you want\n" + prod_msg, false, true);
                }
            }
        }
    }

    public class TokenizeAsyncTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            InputStream modelIn = null;
            String msg = strings[0];
            String[] result = new String[]{"Something wrong"};
            try {
                modelIn = getActivity().getAssets().open("en-token.bin");
                TokenizerModel model = new TokenizerModel(modelIn);
                Tokenizer tokenizer = new TokenizerME(model);
                String tokens[] = tokenizer.tokenize(msg);
                Log.d("product", "doInBackground:TokenizeTask " + tokens.toString());
                return tokens;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            String tokens[] = strings;
            new checkTokensForSentenceType().execute(tokens);


            //new CategorizeTask().execute(tokens);
            //new SubCategorizeTask().execute(tokens);
            //new BrandDetectionTask().execute(tokens);
        }
    }

    private class checkTokensForSentenceType extends AsyncTask<String[], Void, Integer> {
        String tokens[];

        @Override
        protected Integer doInBackground(String[]... strings) {
            tokens = strings[0];
            int flag = 0;
            int length = tokens.length;
            int x = 0;
            tokens = strings[0];
            loop:
            for (int i = 0; i < length; ++i) {
                switch (tokens[i].toLowerCase()) {
                    case "hi":
                    case "hello":
                        x = (int) Math.pow(2, 16);
                        break loop;
                    case "what":
                        x = x | 1;
                        break;
                    case "when":
                        x = x | (1 << 1);
                        break;
                    case "where":
                        x = x | (1 << 2);
                        break;
                    case "why":
                        x = x | (1 << 3);
                        break;
                    case "which":
                        x = x | (1 << 4);
                        break;
                    case "how":
                        x = x | (1 << 5);
                        break;
                    case "about":
                        x = x | (1 << 6);
                        break;
                    case "company":
                        x = x | (1 << 7);
                        break;
                    case "delivery":
                    case "deliver":
                    case "delivered":
                        x = x | (1 << 8);
                        break;
                    case "give":
                        x = x | (1 << 9);
                        break;
                    case "order":
                        x = x | (1 << 10);
                        break;
                    case "products":case "product":
                        x = x | (1 << 11);
                        break;
                    case "structures":case "structure":
                        x = x | (1 << 12);
                        break;
                    case "brands":case "brand":
                        x = x | (1 << 13);
                        break;
                    case "yes":
                        x = x | (1 << 14);
                        break;
                    case "no":
                        x = x | (1 << 15);
                        break;
                }
            }
            return x;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("product", "onPostExecute:checktoken " + integer);
            if (integer != 0) {

                if ((integer & 31) != 0) {
                    if ((integer&2049) == 2049/*what products*/ || (integer&2064) == 2064/*which products*/) {
                        generateChatMessage("1).Aluminium\n2).Chemical\n3).Commodity Polymer\n" +
                                "4).Engineering Polymer\n5).Kota stones\n6).Rubber Additives\n" +
                                "7).Steel\n8).Tiles", false, true);
                    } else if ((integer&4114) == 4114/*Which structures*/ || (integer&4097) == 4097 /*What structures*/) {
                        generateChatMessage("1).Sheet\n2).Coil\n3).Ingot\n4).Billets....and 10 others" +
                                ", please specify your structure.", false, true);
                    } else if ((integer&8193) == 8193/*what brands*/ || (integer&8208) == 8208/*which brands*/) {
                        generateChatMessage("1).Sail\n2).Tata\n3).Jindal...and many more", false, true);
                    } else {
                        generateChatMessage("Sorry,I didn't get you", false, true);
                    }

                } else {

                    if (integer == 65536) {
                        generateChatMessage("Hello sir, How can I help you?", false, true);
                    } else if ((integer&16384) != 0) {
                        generateChatMessage("Then please click on the above order to confirm it,we were happy to take your request, have a nice day sir", false, true);
                    } else if ((integer&32768)!=0) {
                        generateChatMessage("then please re-specify wrong entries in above product",false,true);

                    } else if ((integer & (3 << 9)) != 0 /*give order*/) {
                        generateChatMessage("Ok sir ,Will you please specify you order details," +
                                " Please specify one product at a time", false, true);
                    } else if ((integer & (3 << 6)) != 0 /*about company*/ || (integer & 128) != 0) /*company*/ {
                        generateChatMessage("http://www.power2sme.com/ \n Please visit our website" +
                                " for the details about our company.", false, true);
                    } else {
                        generateChatMessage("Sorry,I didn't get you", false, true);
                    }
                }
            } else {
                new CategorizeTask().execute(tokens);
                new SubCategorizeTask().execute(tokens);
                new BrandDetectionTask().execute(tokens);
            }
        }
    }
}
