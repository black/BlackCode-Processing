import java.util.*;
String[] lyrics, finalWords;
Map<String, Integer> mp= new HashMap<String, Integer>();
int count=0;

void setup() {
  size(500, 500);  
  lyrics = loadStrings("song.txt");
  //println(lyrics);
  getRepeatation(lyrics);
  println(mp);
}

void draw() {
  background(-1);
  //  finalWords = getSortedWords(lyrics);
  //  for (int i=0; i<finalWords.length; i++) {
  //  }
}

void getSortedWords(String[] str) {
  //  try (Scanner scan = new Scanner(new BufferedInputStream(System.in))) {
  //    // find word boundaries...
  //    scan.useDelimiter(Pattern.compile("[\\s;:,.'\n]+", Pattern.MULTILINE));
  //    scan.nextLine(); // ignore the count
  //    Set<String> words = new TreeSet<>();
  //    while (scan.hasNext ()) {
  //      String word = scan.next().toLowerCase();
  //      words.add(word);
  //    }
  //    StringBuilder sb = new StringBuilder();
  //    sb.append(words.size()).append("\n");
  //    for (String word : words) {
  //      sb.append(word).append("\n");
  //    } 
  //  }
}

void getRepeatation(String[] str) {
  for (int i=0; i<str.length; i++) {
    count=0;
    for (int j=0; j<str.length; j++) {
      if (str[i].equals(str[j])) {
        count++;
      }
    }
    mp.put(str[i], count);
  }
}

