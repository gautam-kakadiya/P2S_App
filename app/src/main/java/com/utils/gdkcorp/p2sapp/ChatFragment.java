package com.utils.gdkcorp.p2sapp;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import opennlp.tools.util.StringUtil;


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
    private boolean prod_confirmed = false;
    private int contains_int = -1;
    private boolean want_credit = false;
    private int options_flag = -1;
    ArrayList<DetectedProduct> prod_list = new ArrayList<DetectedProduct>();
    public DetectedProduct current_product = new DetectedProduct();
    private int yes_flag = 0;
    private boolean check_subcat_while_brand = false;
    private boolean check_subcat_while_all=false;
    String[] aluminiumSubCat = new String[]{"Sheet"};
    String[] steelSubCat = new String[]{"Sheet", "Beam", "Billet", "Channel", "Coil", "Coulumn", "Flat", "Ingot", "Joist", "Pipe", "Plate", "Purlin",
            "Angle", "Strip", "TMT", "Double-Arm-Decorative-Bracket", "Foundation-Bolts", "Octagonal-Pole", "Rectangualr-Bar", "Rectangular-Tube",
            "Scrap-Profile-Plate", "Single-Arm-Decorative-Bracket", "Square-Bar", "Square-Tube", "Wire-Rods", "Zinc-Ingot"};

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
        edit_chat_msg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handle = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendFunction(textView);
                    handle = true;
                }
                return handle;
            }
        });
        return v;
    }

    private void sendFunction(TextView textView) {
        contains_int = -1;
        if (edit_chat_msg.getText().toString().equalsIgnoreCase("")) {
            edit_chat_msg.setError("Enter some text first");
        } else if (ok_message_flag != 0) {
            String message = edit_chat_msg.getText().toString();
            edit_chat_msg.setText("");
            hideKeyboard(textView);
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
            hideKeyboard(textView);
            generateChatMessage(message, true, false);
            generateTypingResponce();
            new TokenizeAsyncTask().execute(message);
        }
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
        contains_int = -1;
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
    private void generateTypingResponceWithDelay(int delay) {
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
        }, delay);
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

    private void generateChatMessageWithDelay(final String s, final boolean isMe, final boolean replaceLast,int delay) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, delay);
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
                if (prod.getCategory() != null) {
                    if (prod.getCategory().equalsIgnoreCase(category)) {
                        dont_create_product = true;
                        current_prod_no = i;
                        break;
                    }
                }
            }
            if (current_prod_no == -1) {
                DetectedProduct prod = new DetectedProduct();
                prod.setCategory(category);
                prod_list.add(prod);
                current_prod_no = prod_list.size() - 1;
            } else {
                prod_list.get(current_prod_no).setCategory(category);
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
                if (prod.getCategory() == null) {
                    if (prod.getSub_catagory() != null) {
                        generateChatMessageWithDelay("Ok " + prod.getSub_catagory(), false, true,2000);
                        new CheckSubCatagory().execute(prod.getSub_catagory());
                        generateTypingResponceWithDelay(2000);
                    }
                } else if (prod.getSub_catagory() == null) {
                    generateChatMessageWithDelay("Which structure of " + prod.getCategory() + " you want?", false, true,2000);
                    options_flag = 2;
                } else if (prod.getBrand() == null) {
                    check_subcat_while_brand= true;
                    Log.d("product", "while brand ");
                    new CheckSubCatagory().execute(prod.getSub_catagory());

                } else {
                    Log.d("product", "while all ");
                    check_subcat_while_all=true;
                    new CheckSubCatagory().execute(prod.getSub_catagory());

                }
            } else {
                generateChatMessageWithDelay("Sorry,I didn't get you", false, true,2000);
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
                for (int i = 0; i < tokens.length; ++i) {
                    Log.d("product", "doInBackground:TokenizeTask " + tokens[i]);
                }
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
                boolean isInt = isInteger(tokens[i]);
                if (isInt) {
                    contains_int = i;
                }
                switch (tokens[i].toLowerCase()) {
                    case "hi":case "hii":case "hiii":case "helo":case "hey":
                    case "hello":
                        x = 262144;
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
                    case "options":
                    case "option":
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
                    case "place":
                        x = x | (1 << 9);
                        break;
                    case "order":
                        x = x | (1 << 10);
                        break;
                    case "products":
                    case "product":
                        x = x | (1 << 11);
                        break;
                    case "structures":
                    case "structure":
                        x = x | (1 << 12);
                        break;
                    case "brands":
                    case "brand":
                        x = x | (1 << 13);
                        break;
                    case "yes":
                    case "ha":
                    case "haan":
                    case "han":
                        x = x | (1 << 14);
                        break;
                    case "no":
                    case "na":
                    case "naan":
                    case "nan":
                        x = x | (1 << 15);
                        break;
                    case "credit":
                        x = x | (1 << 16);
                        break;
                    case "advance":case "adv":
                        x = x | (1 << 17);
                        break;
                }
            }
            return x;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("product", "onPostExecute:checktoken " + integer);
            int process = 0;
            if (integer != 0) {
                if ((integer & 31) != 0) {
                    if ((integer & 33) == 33/*What options*/ && options_flag != -1) {
                        switch (options_flag) {
                            case 1:
                                generateChatMessageWithDelay("Aluminium\nChemical\nCommodity Polymer\n" +
                                        "Engineering Polymer\nKota stones\nRubber Additives\n" +
                                        "Steel\nTiles", false, true,2000);
                                options_flag = -1;
                                break;
                            case 2:
                                generateChatMessageWithDelay("Sheet\nCoil\nIngot\nBillets....and 10 others" +
                                        ", please specify your structure.", false, true,2000);
                                options_flag = -1;
                                break;
                            case 3:
                                generateChatMessageWithDelay("Sail\nTata\nJindal...and many more", false, true,2000);
                                options_flag = -1;
                                break;
                        }
                    } else if ((integer & 2049) == 2049/*what products*/ || (integer & 2064) == 2064/*which products*/) {
                        generateChatMessageWithDelay("Aluminium\nChemical\nCommodity Polymer\n" +
                                "Engineering Polymer\nKota stones\nRubber Additives\n" +
                                "Steel\nTiles", false, true,2000);
                    } else if ((integer & 4114) == 4114/*Which structures*/ || (integer & 4097) == 4097 /*What structures*/) {
                        generateChatMessageWithDelay("Sheet\nCoil\nIngot\nBillets....and 10 others" +
                                ", please specify your structure.", false, true,2000);
                    } else if ((integer & 8193) == 8193/*what brands*/ || (integer & 8208) == 8208/*which brands*/) {
                        generateChatMessageWithDelay("Sail\nTata\nJindal...and many more", false, true,2000);
                    } else {
                        generateChatMessageWithDelay("Sorry,I didn't get you", false, true,2000);
                    }

                } else {

                    if (integer == 262144/*hi*/) {
                        generateChatMessageWithDelay("Hello sir, How can I help you?", false, true,2000);
                    } else if ((integer & 16384) == 16384 /*yes*/) {
                        if (current_prod_no != -1 && yes_flag == 0) {
                            generateChatMessageWithDelay("ok sir we have your order", false, true,2000);
                            prod_confirmed = true;
                            generateTypingResponceWithDelay(2000);
                            generateChatMessageWithDelay("What will be your payment term?\nCredit\nor\nAdvance", false, true,4000);
                        } else if (yes_flag == 2) {
                            generateChatMessageWithDelay("Ok",false,true,2000);
                            prod_list.get(current_prod_no).setCategory("Steel");
                            generateTypingResponceWithDelay(2000);
                            DetectedProduct prod = prod_list.get(current_prod_no);
                            generateChatMessageWithDelay("Which brands " + prod.getCategory() + " " + prod.getSub_catagory() + " you want?", false, true,4000);
                            options_flag = 3;
                            yes_flag=0;
                        } else {
                            generateChatMessageWithDelay("Sorry,I didn't get you", false, true,2000);
                        }
                    } else if ((integer & 32768) == 32768 /*no*/) {
                        if(yes_flag==2){
                            generateChatMessageWithDelay("We dont have "+prod_list.get(current_prod_no).getSub_catagory()+
                                    " in any other metal category,SORRY",false,true,2000);
                            generateTypingResponceWithDelay(2000);
                            generateChatMessageWithDelay("But still if you have other orders you can specify",false,true,4000);
                            yes_flag=0;
                            prod_list.clear();
                            current_prod_no = -1;
                            prod_confirmed = false;
                            contains_int = -1;
                            want_credit = false;
                        }else {
                            generateChatMessageWithDelay("then please re-specify wrong entries in above product", false, true,2000);
                        }
                    } else if ((integer & (3 << 9)) == (3 << 9) /*give order*/) {
                        generateChatMessageWithDelay("Ok sir ,Will you please specify your order details," +
                                " Please specify one product at a time", false, true,2000);
                    } else if ((integer & (3 << 6)) == (3 << 6) /*about company*/ || (integer & 128) != 0) /*company*/ {
                        generateChatMessageWithDelay("http://www.power2sme.com/ \n Please visit our website" +
                                " for the details about our company.", false, true,2000);
                    } else if ((integer & 65536) == 65536 /*Credit*/) {
                        if (prod_confirmed == true) {
                            want_credit = true;
                            generateChatMessageWithDelay("How many days Credit you want?\n1).7 days\n2).15 days\n" +
                                    "3).30 days", false, true,2000);
                        }
                    } else if ((integer & 131072) == 131072 /*Advance*/) {
                        if (prod_confirmed == true) {
                            generateChatMessageWithDelay("ok sir , your order is complete ,WE ARE HAPPY " +
                                    "TO DO BUSSINESS WITH YOU , HAVE A NICE DAY SIR", false, true,2000);
                            prod_list.get(current_prod_no).setPayment_term("Advance");
                            current_product = prod_list.get(current_prod_no);
                            prod_list.clear();
                            current_prod_no = -1;
                            prod_confirmed = false;
                            contains_int = -1;
                            want_credit = false;
                        }
                    } else {
                        process = 1;
                    }
                }
            }
            if (integer == 0 && contains_int != -1) {
                Log.d("product", "inthis ");
                if (prod_confirmed == true && want_credit == true) {
                    if (Integer.parseInt(tokens[contains_int]) == 7 || Integer.parseInt(tokens[contains_int]) == 15 || Integer.parseInt(tokens[contains_int]) == 30) {
                        generateChatMessageWithDelay("ok sir , your order is complete ,WE ARE HAPPY " +
                                "TO DO BUSSINESS WITH YOU , We will  get back to you soon sir", false, true,2000);
                        prod_list.get(current_prod_no).setPayment_term("Credit of "+Integer.parseInt(tokens[contains_int])+" Days");
                        current_product = prod_list.get(current_prod_no);
                        prod_list.clear();
                        current_prod_no = -1;
                        prod_confirmed = false;
                        contains_int = -1;
                        want_credit = false;
                    } else {
                        generateChatMessageWithDelay("Please enter 7 , 15 , 30 days", false, true,2000);
                    }
                } else {
                    generateChatMessageWithDelay("Sorry,I didn't get you", false, true,2000);
                }
            } else if (integer == 0 || process != 0) {
                Log.d("product", "In else if ");
                new CategorizeTask().execute(tokens);
                new SubCategorizeTask().execute(tokens);
                new BrandDetectionTask().execute(tokens);
            }
        }
    }

    public class CheckSubCatagory extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            if(check_subcat_while_brand==false && check_subcat_while_all==false) {
                int c = 0;
                int length = aluminiumSubCat.length;
                for (int i = 0; i < length; ++i) {
                    if (strings[0].equalsIgnoreCase(aluminiumSubCat[i])) {
                        c = 5;
                        break;
                    }
                }
                length = steelSubCat.length;
                for (int i = 0; i < length; ++i) {
                    if (strings[0].equalsIgnoreCase(steelSubCat[i])) {
                        ++c;
                        break;
                    }
                }
                return c;
            }else if(check_subcat_while_brand==true){
                int c=2;
                DetectedProduct prod = prod_list.get(current_prod_no);

                if(prod.getCategory().equalsIgnoreCase("Aluminium")) {
                    int length = aluminiumSubCat.length;
                    for (int i = 0; i < length; ++i) {
                        if (prod.getSub_catagory().equalsIgnoreCase(aluminiumSubCat[i])){
                            c=8;
                            break;
                        }
                    }
                }else{
                    int length = steelSubCat.length;
                    for (int i = 0; i < length; ++i) {
                        if (prod.getSub_catagory().equalsIgnoreCase(steelSubCat[i])){
                            c=8;
                            break;
                        }
                    }
                }
                check_subcat_while_brand=false;
                return c;

            }else if(check_subcat_while_all==true){
                int c=3;
                DetectedProduct prod = prod_list.get(current_prod_no);

                if(prod.getCategory().equalsIgnoreCase("Aluminium")) {
                    int length = aluminiumSubCat.length;
                    for (int i = 0; i < length; ++i) {
                        if (prod.getSub_catagory().equalsIgnoreCase(aluminiumSubCat[i])){
                            c=7;
                            break;
                        }
                    }
                }else{
                    int length = steelSubCat.length;
                    for (int i = 0; i < length; ++i) {
                        if (prod.getSub_catagory().equalsIgnoreCase(steelSubCat[i])){
                            c=7;
                            break;
                        }
                    }
                }
                check_subcat_while_all=false;
                return c;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Log.d("product", "checkInteger " + integer);
            if (integer==6) {
                generateChatMessageWithDelay("Can you please specify more specifically, which product you want?", false, true,4000);
                options_flag = 1;
            } else if(integer==1){
                generateChatMessageWithDelay("Do you want steel " + prod_list.get(current_prod_no).getSub_catagory() + "?", false, true,4000);
                yes_flag = 2;
            } else if(integer==8){
                DetectedProduct prod = prod_list.get(current_prod_no);
                generateChatMessageWithDelay("Which brands " + prod.getCategory() + " " + prod.getSub_catagory() + " you want?", false, true,4000);
                options_flag = 3;
            } else if(integer==2 || integer==3){
                DetectedProduct prod = prod_list.get(current_prod_no);
                generateChatMessageWithDelay("SORRY sir, We dont have "+prod.getCategory()+" "+prod.getSub_catagory(),false,true,4000);
                generateTypingResponceWithDelay(4000);
                generateChatMessageWithDelay("But still if you have other orders you can specify",false,true,5000);
                prod_list.clear();
                current_prod_no = -1;
                prod_confirmed = false;
                contains_int = -1;
                want_credit = false;
            } else if(integer==7){
                DetectedProduct prod = prod_list.get(current_prod_no);
                String prod_msg = prod.generateProduct();
                generateChatMessageWithDelay("Is this is what you want?\nType 'Yes' to Confirm it\n" + prod_msg, false, true,4000);
                generateTypingResponceWithDelay(4000);
                generateChatMessageWithDelay("Or continue to modify your product", false, true,5000);
            }else{
                generateChatMessageWithDelay("please re-specify your product",false,true,4000);
            }
        }
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
