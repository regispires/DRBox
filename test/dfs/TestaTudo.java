package dfs;

import helper.ByteUtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import dfs.client.ClientModuleImplTest;
import dfs.server.ds.DirectoryServiceImplTest;
import dfs.server.fs.FileAttributesTest;
import dfs.server.fs.FileServiceImplTest;


@RunWith(Suite.class)
@SuiteClasses({FileServiceImplTest.class, FileAttributesTest.class,
    ByteUtilsTest.class, UfidTest.class, DirectoryServiceImplTest.class,
    ClientModuleImplTest.class})
public class TestaTudo {
	public static final String TEST_PATH = "./testing/";
}
