

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

// TODO: 28/05/19 scope problem
/**
 * we need to specify scoope for blocks using symbol table class
 * we can do this as it every new scope it push new table and make last table
 * as its parent to navigate until reach main scoope which is parent is null .
 *
 * >> so we need to replace any put and get in code used hash table to symbol table methods
 * and check on value in symbol table if we need
 *
 * */
// TODO: 27/05/19 Print in file insted of consol
// TODO: 27/05/19 update project readme
// TODO: 27/05/19 provide simple testCases
public class myVisitor extends CoolBaseVisitor<Value> {

    //id , val
    private Hashtable <String, Value> memory = new Hashtable <String, Value>();
    //id ,temp
    private Hashtable <String, String> tmemory = new Hashtable<String,String>();
    //temp ,val
    private Hashtable <Value, Value> tval = new Hashtable<Value,Value>();
//
//    BufferedWriter writer;
//
//    {
//
//        try {
//            writer = new BufferedWriter(new FileWriter("3AddCode.txt"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
    * @Class The SymbolTable  stores named identifiers, allows them to be located by name, and
       keeps track of its parent.
    * */
    public static class SymbolTable {
        private Hashtable symbolTable;
        public static Stack<SymbolTable> scopeStack = new Stack<>();
        protected SymbolTable outerSymbolTable;
        public SymbolTable(SymbolTable table) {
            symbolTable = new Hashtable();
            outerSymbolTable = table;
        }
        public void put(String token, String value) {
            symbolTable.put(token, value);
            System.out.println(value+ "  is added in symbol table");

        }
        public boolean get(String value) {
            for (SymbolTable tab = this ; tab != null ; tab = tab.outerSymbolTable) {
                if(tab.symbolTable.containsValue(value)){
                    System.out.println(value +"  is found in symbol table");
                    return true;
                }
            }
            System.out.println(value + "  is NOT found in symbol table");

            return false;
        }
    }

    /**
     * @Class generate temp used in 3 add code to print*
     * */
    static public class Temp{
        static int count = 0;
        int number;
        public Temp() { number = ++count; }
        public String toString() { return "t" + number; }
    }
    /**
     * @Class generate labels used in 3 add code to print*
     * */
    static public class Label{
        static int count = 0;
        int number;
        public Label() { number = ++count; }
        public String toString() { return "L" + number; }
    }

    /**
     * @Class :assignment
     * @production :ID ASSIGN_OPERATOR expr # assignment
     * @Description :  t1 = 6 >> x= t1
     * */
    @Override public Value visitAssignment(CoolParser.AssignmentContext ctx) {

        String id = ctx.ID().getText();
        Value value = visit(ctx.expr());
        Temp temp  = new Temp();
        if (!SymbolTable.scopeStack.peek().get(id)){
            System.err.println("undeclared variable at :" + id);
        }
        memory.put(id, value);
        tmemory.put(id,temp.toString());
        tval.put(new Value(temp.toString()),value);

        System.out.println(temp.toString()+ " = " + value);
        System.out.println(id.toString() + " = " + temp.toString());

        return new Value(temp.toString());

    }

    /**
     * @Class :int
     * @production :INTEGER # int
     * @Description : return integer specified in production
     * */
    @Override public Value visitInt(CoolParser.IntContext ctx) {

        return new Value(Integer.valueOf(ctx.getText()));
    }

    /**
     * @Class :  string
     * @production :LITERAL # string
     * @Description : return litral specified in production
     * */
    @Override public Value visitString(CoolParser.StringContext ctx) {

        return  new Value(String.valueOf(ctx.getText()));
    }

    /**
     * @Class :  false
     * @production :FALSE # false
     * @Description : false برضوا
     * */
    @Override public Value visitFalse(CoolParser.FalseContext ctx) {

        return new Value(Boolean.valueOf(ctx.getText()));
    }

    /**
     * @Class :  true
     * @production :TRUE # true
     * @Description : true
     * */
    @Override public Value visitTrue(CoolParser.TrueContext ctx) {

        return new Value(Boolean.valueOf(ctx.getText()));
    }

    /**
     * @Class :  id
     * @production :ID # id
     * @Description : get id
     * */
    @Override public Value visitId(CoolParser.IdContext ctx) {
        String id = ctx.getText();
        Value value = memory.get(id);
//        if(value == null) {
//            throw new RuntimeException("no such variable: " + id);
//        }
        String t = tmemory.get(id);

        return new Value(t);
    }

    /**
     * @Class :  paran
     * @production : OPENP_RANSIS expr CLOSE_PRANSIS # parentheses
     * @Description : visit pass
     * */
    @Override public  Value visitParentheses(CoolParser.ParenthesesContext ctx) {

        return visit(ctx.expr());
    }

    /** check this>> wafaa
     * @Class :  string
     * @production : NOT expr # boolNot
     * @Description : t1 = !t2
     * */
    @Override public Value visitBoolNot(CoolParser.BoolNotContext ctx) {

        Value value = this.visit(ctx.expr());
        Temp temp = new Temp();
        System.out.println(temp.toString()+ " = " + value);

        return new Value(!value.asBoolean());
    }
    /**
     * @Class :  addition
     * @production :| expr PLUS expr # plus
     * @Description : t3 = t2 + t1;
     * */
    @Override public Value visitPlus(CoolParser.PlusContext ctx) {

        Value left = this.visit(ctx.expr(0)); //t VALUE
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " + " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  subtract
     * @production : expr MINUS expr # minus
     * @Description : t3 = t2 - t1
     * */
    @Override public Value visitMinus(CoolParser.MinusContext ctx) {

        Value left = this.visit(ctx.expr(0)); //t VALUE
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " - " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  multiplication
     * @production : expr MULTIPLY expr # multiplication
     * @Description : t3 = t2 * t1
     * */
    @Override public Value visitMultiplication(CoolParser.MultiplicationContext ctx) {

        Value left = this.visit(ctx.expr(0)); //t VALUE
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " * " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  Division
     * @production :expr DIVIDED expr # division
     * @Description : t3 = t2 / t1
     * */
    @Override public Value visitDivision(CoolParser.DivisionContext ctx) {

        System.out.println("visit Division");

        Value left = this.visit(ctx.expr(0)); //t VALUE
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " / " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  less than or equal
     * @production : expr LESS_THAN_OR_EQUAL expr # lessOREqual
     * @Description : t3 = t2 <= t1;
     * */
    @Override public Value visitLessOREqual(CoolParser.LessOREqualContext ctx) {

        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " <= " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  less than
     * @production :expr SMALLER_THAN expr # smallerThan
     * @Description : t3 = t2 < t1
     * */
    @Override public Value visitSmallerThan(CoolParser.SmallerThanContext ctx) {

        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " < " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  negate
     * @production :INTEGER_NEGATIVE expr # negative
     * @Description :t2 = ~ t1
     * */
    @Override public Value visitNegative(CoolParser.NegativeContext ctx) {

        Value eval = this.visit(ctx.expr());
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + " ~ " + eval);

        return new Value(temp.toString());
    }

    /**
     * @Class :  Equality
     * @production : expr EQUAL expr # equal
     * @Description : t3 = t2 == t1
     * */
    @Override public Value visitEqual(CoolParser.EqualContext ctx) {
        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));
        Temp temp = new Temp();
        System.out.println(temp.toString() + " = " + left + " == " + right);

        return new Value(temp.toString());
    }

    /**
     * @Class :  if
     * @production :IF expr THEN expr ELSE expr FI # if
     * @Description : if exp goto l1 exp  l1: exp
     * */
    @Override public Value visitIf(CoolParser.IfContext ctx) {
        Label label = new Label();
        System.out.println("IF ");
        this.visit(ctx.expr(0));
        System.out.println("go to " + label.toString()  + "\n" );
        this.visit(ctx.expr(2));
        System.out.println("\n" + label.toString() +" : ");
        this.visit(ctx.expr(1));

        return Value.VOID;
    }

    /**
     * @Class :  is void
     * @production :ISVOID expr # isvoid
     * @Description : is void ? true
     * */
    //need test i don't think we need it اساسا
    @Override public Value visitIsvoid(CoolParser.IsvoidContext ctx) {
        Temp temp  = new Temp();
        System.out.println(temp.toString() +"Is Void");
        Value val = this.visit(ctx.expr());
        return new Value(temp.toString());
    }

    /**
     * @Class :  while
     * @production :WHILE expr LOOP expr POOL # while
     * @Description : structure while loop : l1  expr+ goto l1
     * */
    @Override public Value visitWhile(CoolParser.WhileContext ctx) {

        Label label = new Label();
        System.out.println("Loop: " + label.toString()  + "\n" );
        System.out.println("While");
        this.visit(ctx.expr(0));
        this.visit(ctx.expr(1));
        System.out.println("go to " + label.toString() + "/n");
        return Value.VOID;
    }

    /**
     * @Class :  Case
     * @production :CASE expr OF (ID COLUN TYPE CASE_ARROW expr SEMICOLUN)+ESAC # case
     *
     *  under constrction yet
     * */
    @Override public Value visitCase(CoolParser.CaseContext ctx) { return visitChildren(ctx); }

    /**
     * @Class :  blocks
     * @production : OPEN_CURLY (expr SEMICOLUN)+ CLOSE_CURLY # block
     * @Description : define scope of work
     * */
    //need test >> null pointer
    @Override public Value visitBlock(CoolParser.BlockContext ctx) {

        if (SymbolTable.scopeStack.empty()){ // لو الستاك فاضي يبقى دا أول سكوب outerTable= null
            SymbolTable.scopeStack.push(new SymbolTable(null));
        }else {
            SymbolTable.scopeStack.push(new SymbolTable(SymbolTable.scopeStack.peek()));
            //push the current table in the stack , implicitly set the current table to new empty table
            System.out.println("table id pushed" + SymbolTable.scopeStack.peek().toString());

        }


        Temp temp = new Temp();
        Value data= null;
        System.out.println(temp.toString() + " Start Block :");
        for (int i=0; i<(ctx.getChildCount()-2)/2; i++){
            data = visit(ctx.expr(i));
        }

        SymbolTable.scopeStack.pop();
        return new Value(temp.toString());
    }

    /**
     * @Class : property
     * @production : ID COLUN TYPE (ASSIGN_OPERATOR expr)? # property
     * @Description : Variable identification and assigment
     * */
    @Override public Value visitProperty(CoolParser.PropertyContext ctx) {
        Temp temp = new Temp();
        if(SymbolTable.scopeStack.peek().symbolTable.containsValue(ctx.ID().getText())){
            System.err.println("duplicate of declaration at ^" + ctx.ID().getText());
            return Value.VOID;
        }
        else {
            SymbolTable.scopeStack.peek().put("ID", ctx.ID().getText());
            if (ctx.getChildCount() > 3) {
                System.out.println(temp.toString() +
                        ctx.ID().getText() + " = " + visit(ctx.expr()) + "\n");
            }

            return new Value(temp.toString());
        }
    }

    /**
     * @Class : method
     * @production : ID OPENP_RANSIS (formal (COMMA formal)*)* CLOSE_PRANSIS COLUN TYPE OPEN_CURLY expr CLOSE_CURLY # method
     * @Description : method in form foo(): Int { 1 }
     * */


    @Override public Value visitMethod(CoolParser.MethodContext ctx) {
        System.out.println(ctx.ID().getText()+":\n");
        System.out.println("BeginFunc \n");
        Temp t = new Temp();
        visit(ctx.expr());
        System.out.println(t.toString() +" Return " + "\n");
        System.out.println("EndFunc;\n");

        return new Value(t.toString());
    }
    //expr (AT TYPE)? DOT ID OPENP_RANSIS (expr (COMMA expr)*)* CLOSE_PRANSIS # methodCall
    @Override public Value visitMethodCall(CoolParser.MethodCallContext ctx) {
        Temp t = new Temp();

        int counter = 0;
        if(ctx.getChildCount() > 3){// 3 because initially we have 3 tokens for a function: functionName, ( and ) For example, foo().
            counter++;
            System.out.println(" PushParam " + visit(ctx.expr(0)) + "\n");
            for(int i = 0 ; i < (ctx.getChildCount() - 4) / 2 ; i++){
                // The above calculation to exclude the comma if there are more than one parameter as we want only the parameters.
                counter++;
                System.out.println(" PushParam " + visit(ctx.expr(i + 1)) + "\n");
            }
        }
        System.out.println(t.toString() + " LCall " + ctx.ID().getText() + "\n");
        System.out.println(" PopParams " + counter*4 + "\n");
        System.out.println("heere"+t.toString());
        return new Value(t.toString());

    }



    /**
     * @Class : own method call
     * @production :ID OPENP_RANSIS (expr (COMMA expr)*)* CLOSE_PRANSIS # ownMethodCall
     * @Description : method in form id (params)
     * */
    @Override public Value visitOwnMethodCall(CoolParser.OwnMethodCallContext ctx) {
        Temp t = new Temp();
        return new Value(t.toString());
    }

    /**
     * @Class : let
     * @production : LET ID COLUN TYPE (ASSIGN_OPERATOR expr)? (COMMA ID COLUN TYPE (ASSIGN_OPERATOR expr)?)* IN expr # letIn
     * @Description : Declare variable in scope
     * */
//    @Override public Value visitLetIn(CoolParser.LetInContext ctx) {
//      String id = ctx.ID().getText();
//
//        return visitChildren(ctx);
//    }


}