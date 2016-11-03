// javac -Xlint WebCrawlerApp.java
// java -cp . WebCrawlerApp
import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class WebCrawlerApp {
	public static void main(String[] args) {
		testInternet1();
	}

	private static void testInternet1() {
		Internet internet = createInternet1();
		System.out.println(Arrays.deepToString(internet.crawl()));
	}

	private static Internet createInternet1() {
		Page page1 = new Page();
		page1.setAddress("http://foo.bar.com/p1");
		String[] links1 = {"http://foo.bar.com/p2","http://foo.bar.com/p3","http://foo.bar.com/p4"};
		page1.setLinks(links1);

		Page page2 = new Page();
		page2.setAddress("http://foo.bar.com/p2");
		String[] links2 = {"http://foo.bar.com/p2","http://foo.bar.com/p4"};
		page2.setLinks(links2);

		Page page3 = new Page();
		page3.setAddress("http://foo.bar.com/p4");
		String[] links3 = {"http://foo.bar.com/p5","http://foo.bar.com/p1","http://foo.bar.com/p6"};
		page3.setLinks(links3);

		Page page4 = new Page();
		page4.setAddress("http://foo.bar.com/p5");
		String[] links4 = {};
		page4.setLinks(links4);

		Page page5 = new Page();
		page5.setAddress("http://foo.bar.com/p6");
		String[] links5 = {"http://foo.bar.com/p7","http://foo.bar.com/p4","http://foo.bar.com/p5"};
		page5.setLinks(links5);

		Internet internet = new Internet();
		Page[] pages = {page1,page2,page3,page4,page5};
		internet.setPages(pages);

		return internet;
	}
}

class Internet {
	private static Map<String, Boolean> successAddresses;
	private static Map<String, Boolean> skippedAddresses;
	private static Map<String, Boolean> errorAddresses;
	private static Map<String, Page> pagesHash;
	private List<Page> pages;

	public Internet() {
		// store links in a concurrent hash tables so
		// that insertion and lookup are both O(1) time
		successAddresses = new ConcurrentHashMap<String, Boolean>();
		skippedAddresses = new ConcurrentHashMap<String, Boolean>();
		errorAddresses = new ConcurrentHashMap<String, Boolean>();
		pagesHash = new ConcurrentHashMap<String, Page>();
		pages = new ArrayList<Page>();
	}

	// returns list of String lists in the following order:
	//   Success
	//   Skipped
	//   Error
	public Object[][] crawl() {

		// assumes that internet contains at least one page
		Page p = pages.get(0);
		crawlPage(pages.get(0).getAddress());

		Object[] successArray = successAddresses.keySet().toArray();
		Object[] skippedArray = skippedAddresses.keySet().toArray();
		Object[] errorArray = errorAddresses.keySet().toArray();
		Object[][] ret = {successArray, skippedArray, errorArray};
		return ret;
	}

	private void crawlPage(String address) {
		if (pagesHash.containsKey(address)) {
			Page p = pagesHash.get(address);
			if (!successAddresses.containsKey(address)) {
				successAddresses.put(address, true);

				for (int i = 0; i < p.getLinksLength(); i++) {
					String indexedLink = p.getLink(i);
					crawlPage(indexedLink);
				}
			}
			else if (!skippedAddresses.containsKey(address)) {
				skippedAddresses.put(address, true);
			}
		}
		else if (!errorAddresses.containsKey(address)) {
			errorAddresses.put(address, true);
		}
	}

	public void setPages(Page[] ps) {
		pages = Arrays.asList(ps);

		for (int i = 0; i < pages.size(); i++) {
			Page p = pages.get(i);
			String address = p.getAddress();
			pagesHash.put(address, p);
		}
	}
}

class Page {
	private String address;
	public ArrayList<String> links;

	public Page() {
		address = "";
		links = new ArrayList<String>();
	}

	public String getAddress() {
		return address;
	}

	public String getLink(int i) {
		return links.get(i);
	}

	public int getLinksLength() {
		return links.size();
	}

	public void setAddress(String s) {
		address = s;
	}

	public void setLinks(String[] ls) {
		links = new ArrayList<String>(Arrays.asList(ls));
	}
}


