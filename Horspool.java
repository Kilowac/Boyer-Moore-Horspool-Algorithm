import java.io.IOException;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;
public class Horspool {
	//commandline commands:
	//Change file from command line: --file <filename>
	//Test; show characters and index: --t
	//Show shift table: --table
	
	/*
	 * Size is the amount of characters that will be in the shift table; opted for all characters that can be printed, as the indexes are found through ascii integers addition and subtraction.
	 * The main method holds code that are more "life improvements," ease of testing, change files from commandline (no need to re-compile), show shift table, if chosen to able to search whatever string you want by typing the
	 * string right next to java HM <string>, else it will default to the first string of the file. While performing IO, the code will check for spaces as to not add any unessicary spaces etc.
	 */
	static final int size = 95;
	static String str = "";
	static int[] table;
	static int m, ind;
	public static File file;
	public static void main(String[] args) throws IOException{
		if (args.length == 0) {
			System.out.println("Usage => java HM [--file <filename>] [--t] [--table]\n\tHM.java: error: the following arguments are required: --file <filename>\n\tTry using the testing .txt files or using '--h' for help");
		}
		String sen = "", hold = "";
		boolean test = false, tab = false;
		int command = -1;
		if(args.length > 1){
			try{
				command = Integer.parseInt(args[1]);
			} catch(Exception e){ }
		}
		if(args.length > 0){
			for(int i = args.length-1; i >= 0; i--){
				if(args[i].equals("--h")){
					System.out.println("");
					System.exit();
				}
				if(args[i].equals("--t"))
					test = true;
				if(args[i].equals("--file")){
					if(i+1 < args.length){
						try{
							file = new File(args[i+1]);
						} catch(Exception e){
							System.err.println("File not found.");
							System.exit(0);
						}
					} 
				}
				if(args[i].equals("--table"))
					tab = true;
			}
		}
		Scanner input = new Scanner(file);
		if(args.length > 0 && !(args[0].equals("--t")) && !(args[0].equals("--file")) && !(args[0].equals("--table"))){
			str = args[0];
		} else{
			str = input.next();
		}
		sen = input.next();
		while(input.hasNext()){
			hold = input.nextLine();
			sen += hold.length() == 0 || hold.charAt(0) == ' ' ? hold : String.format(" %s", hold);
		}
		m = str.length();

		table = new int[size];
		for(int i = 0; i < size; i++)
			table[i] = m;
		ind = 0;
		for(int i = 0; i < m-1;i++){
			ind = ((int)str.charAt(i))-32;
			table[ind] = m-1-i;
		}
		comparisons = 0;
		long start, stop, overhead;
		start = System.nanoTime();
		stop = System.nanoTime();
		overhead = stop-start;
		start = System.nanoTime();
		ind = match(sen);
		stop = System.nanoTime();
		start = stop-start-overhead;
		if(test)
			testing(sen);
		if(tab)
			pst();
		System.out.printf("\nString: %s\nIndex: ", str);
		if(ind < 0 )
			System.out.println(ind);
		else
			System.out.printf("%d - %d\n", ind, (ind+(m-1)));
		System.out.printf("Comparisons: %d\n\nTime(ns): %d\n", comparisons, start);
		/* This was used for data automation
		switch(command){
			case 1://time
				return start;
			case 2://comparisons
				return (new Integer(comparisons)).longValue();
			case 3:
				return (new Integer(ind)).longValue();
		}	
		return start;//*/
	}
	
	/*
	 * This will perform the comparisons. index is where the index is currently at in the block of text, i is used for the pattern; inc is to shift index back at the end in the event of a mismatch.
	 * If there is a mismatch, check in with the table[] and finding it's index by subtracting 32 from their ascii number, and reset i to m, as it will be decremented to m-1 next pass through.
	 * In the case of a match, decrement index for next comparison in the pattern and increment inc to remember how far to shift back to m-1, if i == 0 there is a match, return the index
	 */
	static int comparisons = 0;
	private static int match(String comparitor){
		if(comparitor.length() < str.length())
			return -1;
		int index = m-1, inc = 0, c = 0, len = comparitor.length();
		for(int i = m-1; i >= 0 && index < len; i--){
			comparisons++;
			if(str.charAt(i) != comparitor.charAt(index)){
				index += inc;
				c = ((int)comparitor.charAt(index))-32;
				index += table[c];
				i = m;
				inc = 0;
			} else{
				if(i == 0)
					return index;
				index--;
				inc++;
			}
		}
		return -1;
	}
	
	/*
	 * Invoked by the '--t' command, testing() will take the block of text that was just read, 'sen', and store every character into a char[] and print and format all chatacters and associated indexes.
	 * There is also a <k, n>, this is to show what indexes are displayed in the row, to make it easier to find the index.
	 * The whole "WWWWWWWWW" is for just making it easier to identify where the pattern was found at, just by following the line in the index's row.
	 */
	private static void testing(String sen){
		char[] ary = new char[sen.length()];
		int tr = 0;
		for(int i = 0; i < sen.length(); i++)
			ary[i] = sen.charAt(i);
		System.out.printf("Length: %d\nIndexes:\n",ary.length);
		for(int i = 0, j = 0; i<ary.length; ){
			System.out.printf("<%d-%d>\n",i, (i+25)<ary.length ? i+25 : ary.length-1);
			for(j = 0; j< 26 && (i<ary.length); i++, j++)
				System.out.printf("%s   ",String.format("%c%-4c%c",'|',ary[i],'|'));
			System.out.println();
			i-=j;
			tr = i;
			for(j = 0; j < 26 && (i<ary.length); i++, j++)
				System.out.printf("%s   ",String.format("%c%-4d%c",'|',i,'|'));
			System.out.println();
			for(int k = 0; k < 26; k++, tr++){
				if(ind > -1){
					if(tr >= ind && tr < (ind+(m-1))){
						System.out.print("WWWWWWWWW");
						continue;
					} else if(tr == (ind+(m-1))){
						System.out.print("WWWWWW---");
						continue;
					}
					 
				}
				System.out.print("---------");
			}
			System.out.println("\n");
		}
	}
	
	/*
	 * pst stands for Print Shift Table, this is displayed when the '--table' argument is provided when executing the program
	 */
	private static void pst(){
		System.out.print("Shift Table:\n");
		char[] alph = new char[size];
		for(int i = 0; i < size; i++)
			alph[i] = (char)(i+32);
		System.out.println(Arrays.toString(alph));
		System.out.println(Arrays.toString(table));
	}
	
}
