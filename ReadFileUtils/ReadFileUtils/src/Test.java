import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Test {

	private static String txt ="";
	private static Map<Integer, Node> mapNode = new ConcurrentHashMap<Integer, Node>();
	private static List<String> clusterNames = new ArrayList<String>();
	private static String text="{\n\t\"nodes\": [\n";

	private static List<String> generalUselessWords =new ArrayList<String>(
			Arrays.asList("de", "la", "el", ",", ";", "", "en", "del", "los", "las", "demás","bajo","les","hecho","hecho,","pertenece","recibió","realiza","realizan",
					"anualmente","mes","para", "con", "que", "-", "–", "sobre", "dentro", "y", "o", "al", "se", "no", "ni", "si", "(si,","(no,","(para,","hasta","cabo","donde",
					"por", "a", "su", "e", "un", "una", "sus", "según", "1.", "2.", "3.", "4.", "5;", "6.", "y/o","esta","este","durante", "está","más","como","así","hace",
					"ha", "han","es","son", "tiene", "tienen", "id", "lo", "cual", "¿cuántos", "¿la" , "¿el","7.", "/","lleva"));

	private static List<String> specificUselessWords =new ArrayList<String>(
			Arrays.asList("valle", "regional","través","corregimiento","marco","todo", "diferentes", "región", "cada", "años", "nacionales","septiembre",
					"internacional", "espacio", "general","manifestaciones","municipio","fiesta","fiestas","principal","eventos","comunidad",
					"evento","festividad","festival","encuentro","año","celebración","nacional","medio","actividades"));

	@SuppressWarnings("deprecation")
	public static void readCSV(String filePath) {

		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			stream.forEach(createNodeFromLine(mapNode));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.global.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}

	public static Consumer<String> createNodeFromLine(Map<Integer, Node> mapNode) {
		Consumer<String> c = (x) ->{
			String[] splitStr = x.trim().split(",");
			if(!splitStr[0].equals("id")) {
				if (splitStr.length==12) {
					mapNode.put(Integer.parseInt(splitStr[0]),new Node(splitStr[0], splitStr[1], splitStr[2], 
							splitStr[3], splitStr[4], splitStr[5], 
							splitStr[6], splitStr[7], splitStr[8],
							splitStr[9], splitStr[10], splitStr[11]));
				}
			}
		};
		return c;
	}

	@SuppressWarnings("deprecation")
	public static Map<String, List<Integer>> buildHashMapWords(String filePath, int min) {

		Map<String, List<Integer>> mapWord1 = new ConcurrentHashMap<String, List<Integer>>();

		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			stream.forEach(getConsumerWord1(mapWord1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.global.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		return readMapResult(mapWord1,min);
	}

	public static Consumer<String> getConsumerWord1(Map<String, List<Integer>> map) {
		Consumer<String> c = (x) ->{
			String[] splitStr = x.trim().split("\\s+");
			String lineIdAsString = x.trim().split(",")[0];
			Integer lineId = 0;
			if(!"id".equals(lineIdAsString)) {
				lineId = Integer.parseInt(lineIdAsString);
			}
			for(int i=0; i< splitStr.length; i++){
				if (!generalUselessWords.contains(splitStr[i].toLowerCase())){
					Integer value=1;
					List<Integer> newL=new ArrayList<Integer>();



					if (!map.containsKey(splitStr[i].toLowerCase())){
						newL = new ArrayList<Integer>();
						newL.add(value);
						newL.add(lineId);
						map.put(splitStr[i].toLowerCase(), newL);
					}
					else {
						value = map.get(splitStr[i].toLowerCase()).get(0)+1;
						map.get(splitStr[i].toLowerCase()).set(0,value);
						map.get(splitStr[i].toLowerCase()).add(lineId);
						map.put(splitStr[i].toLowerCase(), map.get(splitStr[i].toLowerCase()));
					}


					//	if (map.containsKey(splitStr[i].toLowerCase())){
					//	value = map.get(splitStr[i].toLowerCase())+1;
					//	}
					//	map.put(splitStr[i].toLowerCase(), 7);
				}
			}
		};
		return c;
	}

	public static Map<String, List<Integer>> filterMapResult(Map<String, List<Integer>> map, int min) {
		int i =0;
		Map<String, List<Integer>> mapNew = new ConcurrentHashMap<String, List<Integer>>();
		List<String> values = new ArrayList<>(map.keySet());
		while(i <map.size()) {
			if (map.get(values.get(i)).get(0) >=min){
				mapNew.put(values.get(i) , map.get(values.get(i)));
			}
			i++;
		}
		return mapNew;
	}
	public static Map<String, List<Integer>> readMapResult(Map<String, List<Integer>> mapOld, int min) {

		Map<String, List<Integer>> map = filterMapResult(mapOld, min);
		List<String> values = new ArrayList<>(map.keySet());

		Collections.sort(values, new Comparator<String>() {
			public int compare(String a, String b) {
				// no need to worry about nulls as we know a and b are both in map
				return map.get(b).get(0) - map.get(a).get(0);
			}
		});
		String val = "";//"There are "+ map.size()+ " words which repeat themselves in the file";
		//txt+=val+"\n";
		int i =0;
		while(i <map.size()) {
			val = values.get(i) + "," + map.get(values.get(i))+"\n";
			txt+=val;
			i++;
		}
		//txt+= (char)12;
		return map;
	}
	public static Consumer<String> getConsumerWord2(Map<String, List<Integer>> map, Map<String, List<Integer>> map1Word) {
		Consumer<String> c = (x) ->{
			String[] splitStr = x.trim().split("\\s+");
			String lineIdAsString = x.trim().split(",")[0];
			Integer lineId = 0;
			if(!"id".equals(lineIdAsString)) {
				lineId = Integer.parseInt(lineIdAsString);
			}
			for(int i=0; i< splitStr.length; i++){
				String lowerCase = splitStr[i].toLowerCase();
				if (!generalUselessWords.contains(lowerCase)){

					if(map1Word.containsKey(lowerCase))
					{
						Integer value=1;
						List<Integer> newL;
						if (i+1<splitStr.length){
							if(! generalUselessWords.contains(splitStr[i+1].toLowerCase())){
								String twoWords = splitStr[i].toLowerCase()+ " " +splitStr[i+1].toLowerCase();

								if (!map.containsKey(twoWords)){
									newL = new ArrayList<Integer>();
									newL.add(value);
									newL.add(lineId);
									map.put(twoWords, newL);
								}
								else {
									value = map.get(twoWords).get(0)+1;
									map.get(twoWords).set(0,value);
									map.get(twoWords).add(lineId);
									map.put(twoWords, map.get(twoWords));
								}
							}
						}
					}
				}
			}
		};
		return c;
	}

	public static Consumer<String> getConsumerWord3(Map<String, List<Integer>> map, Map<String, List<Integer>> map1Word) {
		Consumer<String> c = (x) ->{
			String[] splitStr = x.trim().split("\\s+");
			String lineIdAsString = x.trim().split(",")[0];
			Integer lineId = 0;
			if(!"id".equals(lineIdAsString)) {
				lineId = Integer.parseInt(lineIdAsString);
			}
			for(int i=0; i< splitStr.length; i++){
				String lowerCase = splitStr[i].toLowerCase();
				if (!generalUselessWords.contains(lowerCase)){

					if(map1Word.containsKey(lowerCase))
					{
						Integer value=1;
						List<Integer> newL;

						if (i+2<splitStr.length){
							if(!generalUselessWords.contains(splitStr[i+2].toLowerCase())){
								String threeWords = splitStr[i].toLowerCase()+ " " +splitStr[i+1].toLowerCase()+ " " +splitStr[i+2].toLowerCase();

								if (!map.containsKey(threeWords)){
									newL = new ArrayList<Integer>();
									newL.add(value);
									newL.add(lineId);
									map.put(threeWords, newL);
								}
								else {
									value = map.get(threeWords).get(0)+1;
									map.get(threeWords).set(0,value);
									map.get(threeWords).add(lineId);
									map.put(threeWords, map.get(threeWords));
								}
							}
						}
					}
				}
			}
		};
		return c;
	}

	public static Consumer<String> getConsumerWord4(Map<String, List<Integer>> map, Map<String, List<Integer>> map1Word) {
		Consumer<String> c = (x) ->{
			String[] splitStr = x.trim().split("\\s+");
			String lineIdAsString = x.trim().split(",")[0];
			Integer lineId = 0;
			if(!"id".equals(lineIdAsString)) {
				lineId = Integer.parseInt(lineIdAsString);
			}
			for(int i=0; i< splitStr.length; i++){
				String lowerCase = splitStr[i].toLowerCase();
				if (!generalUselessWords.contains(lowerCase)){

					if(map1Word.containsKey(lowerCase))
					{
						Integer value=1;
						List<Integer> newL;
						if (i+3<splitStr.length){
							if(!generalUselessWords.contains(splitStr[i+3].toLowerCase())){
								String threeWords = splitStr[i].toLowerCase()+ " " +splitStr[i+1].toLowerCase()+ " " +splitStr[i+2].toLowerCase()+ " " +splitStr[i+3].toLowerCase();

								if (!map.containsKey(threeWords)){
									newL = new ArrayList<Integer>();
									newL.add(value);
									newL.add(lineId);
									map.put(threeWords, newL);
								}
								else {
									value = map.get(threeWords).get(0)+1;
									map.get(threeWords).set(0,value);
									map.get(threeWords).add(lineId);
									map.put(threeWords, map.get(threeWords));
								}
							}
						}
					}
				}
			}
		};
		return c;
	}

	@SuppressWarnings("deprecation")
	public static void resultByNum(Map<String, List<Integer>> map, Map<String, List<Integer>> map1Word,  Consumer<String> consumer, int min) {

		String filePath = "./src/file.txt";

		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			stream.forEach(consumer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.global.log(Level.SEVERE, e.getMessage(), e.getCause());
		}

		readMapResult(map, min);
	}


	public static void createJsonFile(String destJsonFilePath, String text) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter( destJsonFilePath));
			writer.write( text);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.global.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String csvPath = "./src/Fest_Cauca.csv";
		String filePath = "./src/file.txt";
		String destFilePath = "./src/results.txt";
		String destJsonFilePath = "./src/data.json";
		String destJsonFilePath2 = "./src/data2.json";

		generalUselessWords.addAll(specificUselessWords);
		Map<String, List<Integer>> map1 =buildHashMapWords(filePath, 10);
		Map<String, List<Integer>> map2 = new ConcurrentHashMap<String, List<Integer>>();
		resultByNum(map2, map1,getConsumerWord2(map2,map1), 3);

		//Map<String, List<Integer>> map3 = new ConcurrentHashMap<String, List<Integer>>();
		//Map<String, List<Integer>> map4 = new ConcurrentHashMap<String, List<Integer>>();
		//resultByNum(map3, map1,getConsumerWord3(map3,map1), 3);
		//resultByNum(map4, map1,getConsumerWord4(map4,map1), 3);

		BufferedWriter writer;
		try {
			writer = new BufferedWriter( new FileWriter( destFilePath));
			writer.write( txt);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.global.log(Level.SEVERE, e.getMessage(), e.getCause());
		}
		readCSV(csvPath);
		System.out.println(mapNode.size());
		int i =0;
		List<Integer> values = new ArrayList<>(mapNode.keySet());
		while(i <mapNode.size()) {
			System.out.println(values.get(i));
			i++;
		}
		List<String> nodeLines = createNodeLinesForJson(mapNode);
		List<String> clusterLines =createClusterNodesLines();
		List<String> linkLines = createLinkLinesForJson(mapNode, clusterNames);
		groupNodeLines(nodeLines,false);
		groupNodeLines(clusterLines,true);
		text+="\n\t],\n\t\"links\": [\n";
		groupNodeLines(linkLines,true);
		text+="\n\t]\n}";
		createJsonFile(destJsonFilePath, text);


		text="{\n\t\"nodes\": [\n";
		List<String> nodeLines2 = createNodeLinesForJson(mapNode);
		List<String> clusterLines2 =createClusterNodesDeepLines(txt);
		List<String> deepLinkLines = createDeepLinkLinesForJson(mapNode, txt);

		groupNodeLines(nodeLines2,false);
		groupNodeLines(clusterLines2,true);
		text+="\n\t],\n\t\"links\": [\n";
		groupNodeLines(deepLinkLines,true);
		text+="\n\t]\n}";
		createJsonFile(destJsonFilePath2, text);
	}

	public static void groupNodeLines(List<String> nodeLines, boolean withoutLastCharacter) {
		for (String line:nodeLines) {
			text+=("\t\t"+line+",\n");
		}
		if(withoutLastCharacter) {
			text=text.substring(0, text.length()-2);
		}
		System.out.println(text);
	}

	private static List<String> createNodeLinesForJson(Map<Integer, Node> mapN) {
		int i =0;

		List<String> nodesLines= new ArrayList<String>();

		List<Integer> values = new ArrayList<>(mapN.keySet());
		while(i <mapN.size()) {
			Node n=mapN.get(values.get(i));
			nodesLines.add("{\"id\": \""+n.getId()+"\", \"group\": \""+n.getGroup()+"\"}");
			if(!clusterNames.contains(n.getGroup())) {
				clusterNames.add(n.getGroup());
			}
			i++;
		}
		return nodesLines;
	}

	private static List<String> createClusterNodesLines() {
		int i =0;
		i=0;
		List<String> nodesLines= new ArrayList<String>();
		while(i <clusterNames.size()) {
			nodesLines.add("{\"id\": \""+clusterNames.get(i)+"\", \"group\": \""+clusterNames.get(i)+"\"}");
			i++;
		}
		return nodesLines;
	}

	private static List<String> createClusterNodesDeepLines(String t) {

		String[] lines = t.trim().split("\n");
		List<String> nodesLines= new ArrayList<String>();

		for (String line:lines) {
			String name = line.trim().split(",")[0];

			nodesLines.add("{\"id\": \""+name+"\", \"group\": \""+name+"\"}");
		}
		return nodesLines;
	}

	private static List<String> createDeepLinkLinesForJson(Map<Integer, Node> mapN, String clusterAndIDlist) {
		//{"source": "Napoleon", "target": "Myriel", "value": 1},
		List<String> linkLines= new ArrayList<String>();

		List<Integer> values = new ArrayList<>(mapN.keySet());
		String[] lines = clusterAndIDlist.trim().split("\n");

		for (String line:lines) {
			String[] lineValues = line.trim().split(",");
			lineValues[1]=lineValues[1].substring(1);
			lineValues[lineValues.length-1]=lineValues[lineValues.length-1].substring(0,lineValues[lineValues.length-1].length()-1);
			String name =lineValues[0];
			for (int j = 1; j < lineValues.length; j++) {
				linkLines.add("{\"source\": \""+mapN.get(values.get(Integer.parseInt(lineValues[j].trim()))).getId()+"\", \"target\": \""+name+"\", \"value\": \""+1+"\"}");
			}
		}
		return linkLines;
	}

	private static List<String> createLinkLinesForJson(Map<Integer, Node> mapN, List<String> clusterNames) {
		int i =0;
		//{"source": "Napoleon", "target": "Myriel", "value": 1},
		List<String> linkLines= new ArrayList<String>();

		List<Integer> values = new ArrayList<>(mapN.keySet());
		while(i <mapN.size()) {
			Node n =mapN.get(values.get(i));


			for (String name:clusterNames) {
				if(name.equalsIgnoreCase(n.getGroup())) {
					linkLines.add("{\"source\": \""+n.getId()+"\", \"target\": \""+name+"\", \"value\": \""+1+"\"}");

				}
			}

			i++;
		}
		return linkLines;
	}
}