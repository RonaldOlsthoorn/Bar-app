package nl.groover.bar.gui;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.util.Xml;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.groover.bar.frame.DBHelper;
import nl.groover.bar.frame.Member;
import nl.groover.bar.frame.MemberImporter;

import static org.junit.Assert.*;

/**
 * Created by Ronald Olsthoorn on 5/2/2017.
 */
@RunWith(AndroidJUnit4.class)
public class LedenMainActivityTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void ImportTest() {

        List batch = createTestBatch();
        File f = saveTestBatchToFile(batch);
        importMembers(f);
        compareResult(batch);
    }

    public ArrayList createTestBatch() {

        ArrayList<Member> res = new ArrayList<Member>();

        Member m1 = new Member("Ronald", null, "Olsthoorn", 1);
        Member m2 = new Member("Vincent", "van", "Dijk", 2);
        Member m3 = new Member("Tung", null, "Phan", 3);

        res.add(m1);
        res.add(m2);
        res.add(m3);

        return res;
    }

    public File saveTestBatchToFile(List batch) {

        Context context = InstrumentationRegistry.getTargetContext();
        File appRootDir = context.getExternalFilesDir(null);
        Log.d("TEST", "rootDir: " + appRootDir.getAbsolutePath());

        String dirName = "member_sync";
        File memberSyncDir = new File(appRootDir, dirName);
        memberSyncDir.mkdir();

        String fileName = "import_members.xml";

        File importXmlFile = new File(memberSyncDir, fileName);

        try {
            BufferedOutputStream buf = new BufferedOutputStream(
                    new FileOutputStream(importXmlFile));

            File file = new File(appRootDir, fileName);

            XmlSerializer xmlSerializer = Xml.newSerializer();
            xmlSerializer.setOutput(buf, "UTF-8");

            // start DOCUMENT
            xmlSerializer.startDocument("UTF-8", true);

            xmlSerializer.startTag(null, "import");
            xmlSerializer.startTag(null, "list");
            xmlSerializer.attribute(null, "id", "memberlist");

            Iterator<Member> it = batch.iterator();

            while (it.hasNext()) {

                Member m = it.next();
                xmlSerializer.startTag(null, "member");
                xmlSerializer.attribute(null, "gr_id", Integer.toString(m.getId()));
                xmlSerializer.attribute(null, "firstname", m.getFirstName());

                String prefix = m.getPrefix();

                if(prefix == null)
                    xmlSerializer.attribute(null, "prefix", "");
                else{
                    xmlSerializer.attribute(null, "prefix", prefix);
                }

                xmlSerializer.attribute(null, "lastname", m.getLastName());
                xmlSerializer.endTag(null, "member");

            }

            xmlSerializer.endTag(null, "list");
            xmlSerializer.endTag(null, "import");

            xmlSerializer.endDocument();
            xmlSerializer.flush();

        } catch (IOException e) {
            Log.e("TEST", "creating test xml went wrong", e);
        }

        return importXmlFile;
    }

    public void importMembers(File f) {

        Context context = InstrumentationRegistry.getTargetContext();

        MemberImporter importer = new MemberImporter(context);
        importer.importMembers(f);
        importer.importMembers();
    }

    public void compareResult(List batch) {

        Context context = InstrumentationRegistry.getTargetContext();
        DBHelper DB = DBHelper.getDBHelper(context);

        Cursor c = DB.getMembers();

        c.moveToFirst();

        while (c.getPosition() < c.getCount()) {

            Log.d("TAG", "id: "+c.getInt(0)+ " First name: "+c.getString(1)+" prefix: "+c.getString(2)+" Last name: "+c.getString(3));
            Member m = new Member(c.getString(1), c.getString(2), c.getString(3), c.getInt(0));

            assert(batch.contains(m));
            c.moveToNext();
        }
    }
}