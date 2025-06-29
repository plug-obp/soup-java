package soup.syntax;

import soup.syntax.model.declarations.VariableDeclaration;
import soup.syntax.model.declarations.pieces.NamedPiece;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    Map<String, VariableDeclaration> variableScope = new HashMap<>();
    Map<String, NamedPiece> pieceScope = new HashMap<>();
}
