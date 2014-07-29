package filteringTaskIndexer;

import java.util.ArrayList;

public class BinarySearch {
	public static int binarySearchMin(ArrayList<TweetIDComparable> tweetIDs,
			TweetIDComparable key, int imin, int imax) throws Exception {
		
		System.out.println("In binarySearch");

		while (imax >= imin) {

			int imid = (imax - imin) / 2 + imin;

			if (tweetIDs.get(imid).compareTo(key) < 0) {
				imin = imid + 1;
			} else if (tweetIDs.get(imid).compareTo(key) > 0) {
				imax = imid - 1;
			} else if (tweetIDs.get(imid).compareTo(key) == 0) {
				return imid;
			}
		}
		return imin;
	}

	public static int binarySearchMax(ArrayList<TweetIDComparable> tweetIDs,
			TweetIDComparable key, int imin, int imax) throws Exception {

		System.out.println("In binarySearch");

		while (imax >= imin) {
			int imid = (imax - imin) / 2 + imin;

			if (tweetIDs.get(imid).compareTo(key) < 0) {
				imin = imid + 1;
			} else if (tweetIDs.get(imid).compareTo(key) > 0) {
				imax = imid - 1;
			} else if (tweetIDs.get(imid).compareTo(key) == 0) {
				return imid;
			}
		}
		return imax;
	}
}