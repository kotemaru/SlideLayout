package org.kotemaru.android.slidelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private View mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);
        mToolbar =  findViewById(R.id.toolbar);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.llist_item, R.id.label, DATA);
        mListView.setAdapter(arrayAdapter);

        //ViewGroup mainLayout = (ViewGroup) findViewById(R.id.mainLayout);
        //SlideLayout slideLayout = (SlideLayout) findViewById(R.id.slideLayout);
        //slideLayout.setInnerScrollView(mListView);
        //slideLayout.setSlideTarget(mainLayout);
        //slideLayout.setMaxSlideSize(100);
    }

    public void onHelloClick(View view) {
        Log.e("DEBUG", "onHelloClick");
    }

    private static final String[] DATA = {
            "ASUS",
            "Common Files",
            "CrystalDiskMark",
            "desktop.ini",
            "DVD Maker",
            "Handbrake",
            "Intel",
            "Internet Explorer",
            "Java",
            "Microsoft Games",
            "Microsoft Security Client",
            "Microsoft Silverlight",
            "MongoDB",
            "MSBuild",
            "nodejs",
            "Oracle",
            "RealVNC",
            "Reference Assemblies",
            "SoftPerfect RAM Disk",
            "Uninstall Information",
            "Windows Defender",
            "Windows Journal",
            "Windows Mail",
            "Windows Media Player",
            "Windows NT",
            "Windows Photo Viewer",
            "Windows Portable Devices",
            "Windows Sidebar"
    };


}
