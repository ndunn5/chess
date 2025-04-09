package ui;

import chess.ChessPiece;
import exception.ResponseException;

import java.util.Arrays;

public class PromoteClient {
    public String returnPieceType = "PAWN";

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case ("promote") -> promote(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - ROOK
                - QUEEN
                - BISHOP
                - KNIGHT
                """;
    }

    public String promote(String ... params) throws ResponseException {
        if(params.length == 1){
            switch (params[0]){
                case "ROOK" :
                    Repl.updateState(State.GAMEPLAY);
                    returnPieceType =  "ROOK";
                    break;
                case "QUEEN" :
                    Repl.updateState(State.GAMEPLAY);
                    returnPieceType = "QUEEN";
                    break;
                case "BISHOP" :
                    Repl.updateState(State.GAMEPLAY);
                    returnPieceType = "BISHOP";
                    break;
                case "KNIGHT" :
                    Repl.updateState(State.GAMEPLAY);
                    returnPieceType = "KNIGHT";
                    break;
            }
        } else{
            throw new ResponseException(400, "press help to see expected");
        }
        return returnPieceType;
    }

    public ChessPiece.PieceType getReturnPieceType() {
        switch (returnPieceType){
            case "ROOK" : return ChessPiece.PieceType.ROOK;
            case "QUEEN" : return ChessPiece.PieceType.QUEEN;
            case "BISHOP" : return ChessPiece.PieceType.BISHOP;
            case "KNIGHT" : return ChessPiece.PieceType.BISHOP;
        }
        return ChessPiece.PieceType.PAWN;
    }
}
