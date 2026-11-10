import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> classScope = new HashMap<>();
    private Map<String, Symbol> subroutineScope = new HashMap<>();
    private int staticCount = 0;
    private int fieldCount = 0;
    private int argCount = 0;
    private int varCount = 0;

    public void startSubroutine(){
        subroutineScope.clear();
        argCount = 0;
        varCount = 0;

    }

    // método grande -> deixar mais clean depois
    public void define(String name, String type, SymbolKind kind){
        if(kind == SymbolKind.STATIC || kind == SymbolKind.FIELD) {
            if(classScope.containsKey(name)) {
                throw new IllegalArgumentException(name + " já existe");
            }
            if(kind == SymbolKind.STATIC){
                Symbol symbol = new Symbol(type, kind, staticCount);
                classScope.put(name, symbol);
                staticCount++;
            } else {
                Symbol symbol = new Symbol(type, kind, fieldCount);
                classScope.put(name, symbol);
                fieldCount++;
            }
        }

        if(kind == SymbolKind.ARG || kind == SymbolKind.VAR) {
            if(subroutineScope.containsKey(name)) {
                throw new IllegalArgumentException(name + " já existe");
            }
            if(kind == SymbolKind.VAR){
                Symbol symbol = new Symbol(type, kind, varCount);
                subroutineScope.put(name, symbol);
                varCount++;
            } else {
                Symbol symbol = new Symbol(type, kind, argCount);
                subroutineScope.put(name, symbol);
                argCount++;
            }
        }

    }

    public Symbol resolve(String name) {

        if(subroutineScope.get(name) != null){
            return subroutineScope.get(name);
        } else if (classScope.get(name) != null) {
            return classScope.get(name);
        } else {
            return null;
        }
    }

    public int varCount(SymbolKind kind){
        switch (kind){
            case SymbolKind.STATIC: return staticCount;
            case SymbolKind.FIELD: return fieldCount;
            case SymbolKind.VAR: return argCount;
            case SymbolKind.ARG: return argCount;
        }
        return 0;
    }
}
