package zhaoqi.test1;

public class Test12 {

	public static void main(String[] args) {
		System.out.println(longestCommonPrefix(new String[]{"aa", "ab"}));
	}


	public static  String longestCommonPrefix(String[] strs) {

		StringBuffer prefixBuf = new StringBuffer();


		int charIndex=0;

		int tempInt=-1;


		a:for(int i=0;i<strs.length;i++){
			if (charIndex > strs[i].length() - 1) {
				break a;
			}
			char c = strs[i].charAt(charIndex);
			if(tempInt==-1){

				tempInt=c;
			}else{
				if(c!=tempInt){
					break a;
				}
			}
			if(i==strs.length-1){
				prefixBuf.append(c);
				//要比较下一个元素了
				i=-1;
				tempInt = -1;
				charIndex++;
			}
		}
		return prefixBuf.toString();



	}
}
