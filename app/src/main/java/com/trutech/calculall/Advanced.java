package com.trutech.calculall;

import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Contains the back-end of the advanced calculator mode. The advanced mode will be able to
 * perform the most of the operations of a standard scientific calculator.
 *
 * @version Alpha 2.0
 * @author Alston Lin, Ejaaz Merali
 */
public class Advanced extends Basic {
    //CONSTANTS
    public static final int DEGREE = 1, RADIAN = 2, GRADIAN = 3; //angleMode options
    public static final int DEC = 1, FRAC = 2;
    private static final String FILENAME = "history_advanced";
    private static final Basic INSTANCE = new Advanced();
    //Fields
    protected ArrayList<MultiButton> multiButtons;
    protected boolean switchedAngleMode = false;
    protected int angleMode = DEGREE;
    protected boolean hyperbolic = false, shift = false, mem = false;
    private int fracMode = DEC;

    /**
     * Allows for the Singleton pattern so there would be only one instance.
     * @return The singleton instance
     */
    public static Basic getInstance(){
        return INSTANCE;
    }

    public void setMultiButtons(ArrayList<MultiButton> multiButtons){
        this.multiButtons = multiButtons;
    }

    /**
     * When a Button has been clicked, calls the appropriate method.
     *
     * @param v The Button that has been clicked
     */
    @Override
    public void onClick(View v) {
        //First: If it is a MultiButton
        if (v instanceof MultiButton){
            ((MultiButton)v).onClick();
            updateInput();
            return;
        }
        switch(v.getId()){
            case R.id.equals_button:
                clickEquals();
                break;
            case R.id.shift_button:
                clickShift();
                break;
            case R.id.hyp_button:
                clickHyp();
                break;
            case R.id.mem_button:
                clickMem();
                break;
            case R.id.var_a:
                clickA();
                break;
            case R.id.var_b:
                clickB();
                break;
            case R.id.var_c:
                clickC();
                break;
            case R.id.pi_button:
                clickPi();
                break;
            case R.id.e_button:
                clickE();
                break;
            case R.id.permutation:
                clickPermutation();
                break;
            case R.id.combination:
                clickCombination();
                break;
            case R.id.angle_mode:
                clickAngleMode();
                break;
            case R.id.frac_button:
                clickFrac();
                break;
            case R.id.reciprocal:
                clickReciprocal();
                break;
            case R.id.frac_mode:
                clickFracMode();
                break;
            case R.id.open_bracket_button:
                clickOpenBracket();
                break;
            case R.id.closed_bracket_button:
                clickCloseBracket();
                break;
            default:
                super.onClick(v);
        }
    }

    /**
     * When the user presses the equals Button.
     */
    public void clickEquals() {
        try {
            if (fracMode == DEC) {
                super.clickEquals();
            } else if (fracMode == FRAC) {
                ArrayList<Token> output = JFok.simplifyExpression(tokens);
                display.displayOutput(output);
                saveEquation(tokens, output, FILENAME);
            }
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
        activity.scrollDown();
    }

    public void clickAngleMode() {
        Button angleModeButton = (Button) activity.findViewById(R.id.angle_mode);
        if (angleMode == GRADIAN) {
            convGtoD();
            angleMode = DEGREE;
            angleModeButton.setText(activity.getString(R.string.deg));
        } else if (angleMode == RADIAN) {
            convRtoG();
            angleMode = GRADIAN;
            angleModeButton.setText(activity.getString(R.string.grad));
        } else if (angleMode == DEGREE) {
            convDtoR();
            angleMode = RADIAN;
            angleModeButton.setText(activity.getString(R.string.rad));
        }
        updateInput();
    }

    public void convGtoD() {
        try {
            double val = process();
            if (switchedAngleMode) {
                tokens.set(tokens.size() - 1, new StringToken(" → DEG") {
                });
            } else {
                tokens.add(new StringToken(" → DEG") {
                });
            }
            updateInput();
            Number num = new Number(val * 9 / 10);
            ArrayList<Token> list = new ArrayList<>();
            list.add(num);
            display.displayOutput(list);
            activity.scrollDown();
            switchedAngleMode = true;
        } catch (Exception e) { //User made a mistake
            handleExceptions(e);
        }
    }

    public void convRtoG() {
        try {
            double val = process();
            if (switchedAngleMode) {
                tokens.set(tokens.size() - 1, new Token(" → GRAD") {
                });
            } else {
                tokens.add(new Token(" → GRAD") {
                });
            }
            updateInput();
            Number num = new Number(val * 100 / Math.PI);
            ArrayList<Token> list = new ArrayList<>();
            list.add(num);
            display.displayOutput(list);
            activity.scrollDown();
            switchedAngleMode = true;
        } catch (Exception e) { //User made a mistake
            handleExceptions(e);
        }
    }

    public void convDtoR() {
        //Converts the number displayed from degrees into radians ie multiplies the number by pi/180
        try {
            double val = process();
            if (switchedAngleMode) {
                tokens.set(tokens.size() - 1, new Token(" → RAD") {
                });
            } else {
                tokens.add(new Token(" → RAD") {
                });
            }
            updateInput();
            Number num = new Number(val * Math.PI / 180);
            ArrayList<Token> list = new ArrayList<>();
            list.add(num);
            display.displayOutput(list);
            activity.scrollDown();
            switchedAngleMode = true;
        } catch (Exception e) { //User made a mistake
            handleExceptions(e);
        }
    }

    public void clickFracMode() {
        Button fracModeButton = (Button) activity.findViewById(R.id.frac_button);
        if (fracMode == DEC) {
            fracMode = FRAC;
            fracModeButton.setText(activity.getString(R.string.imp_frac));
        } else if (fracMode == FRAC) {
            fracMode = DEC;
            fracModeButton.setText(activity.getString(R.string.radix));
        }
        updateInput();
        clickEquals();
    }

    /**
     * When the user presses the hyp button. Switches the state of the boolean variable hyperbolic
     */
    public void clickHyp() {
        ToggleButton hypButton = (ToggleButton) activity.findViewById(R.id.hyp_button);
        hyperbolic = !hyperbolic;
        hypButton.setChecked(hyperbolic);
        //Changes the mode for all the Buttons
        for (MultiButton b : multiButtons){
            b.changeMode(shift, hyperbolic);
        }
        hypButton.setChecked(hyperbolic);
        updateInput();
    }

    /**
     * When the user presses the shift button. Switches the state of the boolean variable shift.
     */
    public void clickShift() {
        shift = !shift;
        ToggleButton shiftButton = (ToggleButton) activity.findViewById(R.id.shift_button);
        shiftButton.setChecked(shift);
        //Changes the mode for all the Buttons
        for (MultiButton b : multiButtons){
            b.changeMode(shift, hyperbolic);
        }
        updateInput();
    }

    /**
     * When the user presses the MEM button; toggles memory storage
     */
    public void clickMem() {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button);
        mem = !mem;
        memButton.setChecked(mem);
    }

    /**
     * Stores the a variable into the memory; the assignment itself will occur in the given Command.
     *
     * @param addToOutput The String that will be shown in the output along with the value
     * @param assignment  The assignment command that would be executed
     */
    protected void storeVariable(String addToOutput, Command<Void, Double> assignment) {
        ToggleButton memButton = (ToggleButton) activity.findViewById(R.id.mem_button);
        try {
            double val = process();
            ArrayList<Token> outputList = new ArrayList<>();
            outputList.add(new Number(val));
            outputList.add(new StringToken(addToOutput));
            display.displayOutput(outputList);
            assignment.execute(val);
            mem = false;
            memButton.setChecked(false);
        } catch (Exception e) { //User did a mistake
            handleExceptions(e);
        }
    }

    /**
     * When the user presses the A button
     */
    public void clickA() {
        if (mem) {
            storeVariable("→ A", new Command<Void, Double>() {
                @Override
                public Void execute(Double val) {
                    Variable.a_value = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeA());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the B button
     */
    public void clickB() {
        if (mem) {
            storeVariable("→ B", new Command<Void, Double>() {
                @Override
                public Void execute(Double val) {
                    Variable.b_value = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeB());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }

    /**
     * When the user presses the C button
     */
    public void clickC() {
        if (mem) {
            storeVariable("→ C", new Command<Void, Double>() {
                @Override
                public Void execute(Double val) {
                    Variable.c_value = val;
                    return null;
                }
            });
        } else {
            tokens.add(display.getRealCursorIndex(), VariableFactory.makeC());
            display.setCursorIndex(display.getCursorIndex() + 1);
            updateInput();
        }
    }


    /**
     * When the user presses the ( Button.
     */
    public void clickOpenBracket() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeOpenBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the ) Button.
     */
    public void clickCloseBracket() {
        tokens.add(display.getRealCursorIndex(), BracketFactory.makeCloseBracket());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the 10^x Button.
     */
    public void clickPowerOfTen() {
        Token multiply = OperatorFactory.makeMultiply();
        Token one = DigitFactory.makeOne();
        Token zero = DigitFactory.makeZero();
        Token exp = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), multiply);
        tokens.add(display.getRealCursorIndex() + 1, one);
        tokens.add(display.getRealCursorIndex() + 2, zero);
        tokens.add(display.getRealCursorIndex() + 3, exp);
        tokens.add(display.getRealCursorIndex() + 4, openBracket);
        tokens.add(display.getRealCursorIndex() + 5, PlaceholderFactory.makeBlock());
        tokens.add(display.getRealCursorIndex() + 6, closeBracket);

        exp.addDependency(openBracket);
        exp.addDependency(closeBracket);
        display.setCursorIndex(display.getCursorIndex() + 6);
        updateInput();
    }


    /**
     * When the user presses the e^x Button.
     */
    public void clickExp() {
        clickExponent();
        clickE();
    }

    /**
     * When the user presses the ln(x) Button.
     */
    public void clickLn() {
        Token t = FunctionFactory.makeLn();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the log(x) or log_10(x)Button.
     */
    public void clickLog_10() {
        Token t = FunctionFactory.makeLog_10();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }


    public void clickExponent() {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, PlaceholderFactory.makeBlock());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    public void clickVarRoot() {
        Token root = OperatorFactory.makeVariableRoot();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();
        Bracket b = BracketFactory.makeOpenBracket();

        root.addDependency(b);
        root.addDependency(openBracket);
        root.addDependency(closeBracket);

        if (display.getRealCursorIndex() != 0) {
            //Whats on the numerator depends on the token before
            Token tokenBefore = tokens.get(display.getRealCursorIndex() - 1);
            if (tokenBefore instanceof Digit) {
                LinkedList<Digit> digits = new LinkedList<Digit>();
                int i = display.getRealCursorIndex() - 1;
                while (i >= 0 && tokens.get(i) instanceof Digit) {
                    Token t = tokens.get(i);
                    digits.addFirst((Digit) t);
                    tokens.remove(t);
                    i--;
                }
                tokens.add(display.getRealCursorIndex() - digits.size(), openBracket);
                tokens.addAll(display.getRealCursorIndex() - digits.size() + 1, digits);
                tokens.add(display.getRealCursorIndex() + 1, closeBracket);
                tokens.add(display.getRealCursorIndex() + 2, root);
                tokens.add(display.getRealCursorIndex() + 3, b);

                display.setCursorIndex(display.getCursorIndex() + 3);
                return;
            } else if (tokenBefore instanceof Bracket && ((Bracket) tokenBefore).getType() == Bracket.CLOSE) {
                LinkedList<Token> expression = new LinkedList<Token>();
                int i = display.getRealCursorIndex() - 2;
                int bracketCount = 1;
                expression.add(tokens.remove(display.getRealCursorIndex() - 1));
                while (i >= 0 && bracketCount != 0) {
                    Token t = tokens.remove(i);
                    if (t instanceof Bracket) {
                        Bracket bracket = (Bracket) t;
                        if (bracket.getType() == Bracket.OPEN) {
                            bracketCount--;
                        } else if (bracket.getType() == Bracket.CLOSE) {
                            bracketCount++;
                        }
                    }
                    expression.addFirst(t);
                    i--;
                }
                tokens.add(i + 1, openBracket);
                tokens.addAll(i + 2, expression);

                tokens.add(display.getRealCursorIndex() + 1, closeBracket);
                tokens.add(display.getRealCursorIndex() + 2, root);
                tokens.add(display.getRealCursorIndex() + 3, b);
                display.setCursorIndex(display.getCursorIndex() + 3);
                return;
            }
        }

        tokens.add(display.getRealCursorIndex(), openBracket);
        tokens.add(display.getRealCursorIndex() + 1, PlaceholderFactory.makeBlock());
        tokens.add(display.getRealCursorIndex() + 2, closeBracket);
        tokens.add(display.getRealCursorIndex() + 3, root);
        tokens.add(display.getRealCursorIndex() + 4, b);

        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
    }

    /**
     * When the user presses the sqrt Button.
     */
    @Override
    public void clickSqrt() {
        Token t = FunctionFactory.makeSqrt();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
        updateInput();
    }

    /**
     * When the user presses the x^2 Button.
     */
    public void clickSquare() {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, DigitFactory.makeTwo());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 3);
        updateInput();
    }

    /**
     * When the user presses the x^3 Button.
     */
    public void clickCube() {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, DigitFactory.makeThree());
        tokens.add(display.getRealCursorIndex() + 3, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 3);
        updateInput();
    }

    /**
     * When the user presses the cuberoot Button.
     */
    public void clickCbrt() {
        Token root = OperatorFactory.makeVariableRoot();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();
        Bracket b = BracketFactory.makeOpenBracket();

        root.addDependency(b);
        root.addDependency(openBracket);
        root.addDependency(closeBracket);

        tokens.add(display.getRealCursorIndex(), openBracket);
        tokens.add(display.getRealCursorIndex() + 1, DigitFactory.makeThree());
        tokens.add(display.getRealCursorIndex() + 2, closeBracket);
        tokens.add(display.getRealCursorIndex() + 3, root);
        tokens.add(display.getRealCursorIndex() + 4, b);

        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
    }

    /**
     * When the user presses the FRAC Button.
     */
    public void clickFrac() {
        Token frac = OperatorFactory.makeFraction();
        Token numOpenBracket = BracketFactory.makeNumOpen();
        Token numCloseBracket = BracketFactory.makeNumClose();
        Token denomOpenBracket = BracketFactory.makeDenomOpen();
        Token denomCloseBracket = BracketFactory.makeDenomClose();

        frac.addDependency(numOpenBracket);
        frac.addDependency(numCloseBracket);
        frac.addDependency(denomOpenBracket);
        frac.addDependency(denomCloseBracket);


        if (display.getRealCursorIndex() == 0) {
            tokens.add(display.getRealCursorIndex(), numOpenBracket);
            tokens.add(display.getRealCursorIndex() + 1, PlaceholderFactory.makeBlock());
            display.setCursorIndex(display.getCursorIndex() + 1);
        } else {
            //Whats on the numerator depends on the token before
            Token tokenBefore = tokens.get(display.getRealCursorIndex() - 1);
            if (tokenBefore instanceof Digit) {
                LinkedList<Digit> digits = new LinkedList<Digit>();
                int i = display.getRealCursorIndex() - 1;
                while (i >= 0 && tokens.get(i) instanceof Digit) {
                    Token t = tokens.get(i);
                    digits.addFirst((Digit) t);
                    tokens.remove(t);
                    i--;
                }
                tokens.add(display.getRealCursorIndex() - digits.size(), numOpenBracket);
                tokens.addAll(display.getRealCursorIndex() - digits.size() + 1, digits);

                tokens.add(display.getRealCursorIndex() + 1, numCloseBracket);
                tokens.add(display.getRealCursorIndex() + 2, frac);
                tokens.add(display.getRealCursorIndex() + 3, denomOpenBracket);
                Placeholder p = PlaceholderFactory.makeBlock();
                tokens.add(display.getRealCursorIndex() + 4, p);
                tokens.add(display.getRealCursorIndex() + 5, denomCloseBracket);
                frac.addDependency(p);
                display.setCursorIndex(display.getCursorIndex() + 2);
                return;
            } else if (tokenBefore instanceof Bracket && ((Bracket) tokenBefore).getType() == Bracket.CLOSE) {
                LinkedList<Token> expression = new LinkedList<Token>();
                int i = display.getRealCursorIndex() - 2;
                int bracketCount = 1;
                expression.add(tokens.remove(display.getRealCursorIndex() - 1));
                while (i >= 0 && bracketCount != 0) {
                    Token t = tokens.remove(i);
                    if (t instanceof Bracket) {
                        Bracket b = (Bracket) t;
                        if (b.getType() == Bracket.OPEN) {
                            bracketCount--;
                        } else if (b.getType() == Bracket.CLOSE) {
                            bracketCount++;
                        }
                    }
                    expression.addFirst(t);
                    i--;
                }
                tokens.add(i + 1, numOpenBracket);
                tokens.addAll(i + 2, expression);

                tokens.add(display.getRealCursorIndex() + 1, numCloseBracket);
                tokens.add(display.getRealCursorIndex() + 2, frac);
                tokens.add(display.getRealCursorIndex() + 3, denomOpenBracket);
                Placeholder p = PlaceholderFactory.makeBlock();
                tokens.add(display.getRealCursorIndex() + 4, p);
                tokens.add(display.getRealCursorIndex() + 5, denomCloseBracket);
                frac.addDependency(p);
                display.setCursorIndex(display.getCursorIndex() + 2);
                return;

            } else {
                tokens.add(display.getRealCursorIndex(), numOpenBracket);
                Placeholder p = PlaceholderFactory.makeBlock();
                tokens.add(display.getRealCursorIndex() + 1, p);
                frac.addDependency(p);
            }
        }
        tokens.add(display.getRealCursorIndex() + 2, numCloseBracket);
        tokens.add(display.getRealCursorIndex() + 3, frac);
        tokens.add(display.getRealCursorIndex() + 4, denomOpenBracket);
        Placeholder p = PlaceholderFactory.makeBlock();
        tokens.add(display.getRealCursorIndex() + 5, p);
        tokens.add(display.getRealCursorIndex() + 6, denomCloseBracket);
        display.setCursorIndex(display.getCursorIndex() + 1);
        frac.addDependency(p);
        updateInput();
    }

    /**
     * When the user presses the x^-1 button.
     */
    public void clickReciprocal() {
        Token exponent = OperatorFactory.makeExponent();
        Token openBracket = BracketFactory.makeSuperscriptOpen();
        Token closeBracket = BracketFactory.makeSuperscriptClose();

        tokens.add(display.getRealCursorIndex(), exponent);
        tokens.add(display.getRealCursorIndex() + 1, openBracket);
        tokens.add(display.getRealCursorIndex() + 2, DigitFactory.makeNegative());
        tokens.add(display.getRealCursorIndex() + 3, DigitFactory.makeOne());
        tokens.add(display.getRealCursorIndex() + 4, closeBracket);

        exponent.addDependency(openBracket);
        exponent.addDependency(closeBracket);

        display.setCursorIndex(display.getCursorIndex() + 4);
        updateInput();
        updateInput();
    }

    /**
     * When the user presses the nCk Button.
     */
    public void clickCombination() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makeCombination());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the nPk Button.
     */
    public void clickPermutation() {
        tokens.add(display.getRealCursorIndex(), OperatorFactory.makePermutation());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the e Button.
     */
    public void clickE() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makeE());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }

    /**
     * When the user presses the pi Button.
     */
    public void clickPi() {
        tokens.add(display.getRealCursorIndex(), VariableFactory.makePI());
        display.setCursorIndex(display.getCursorIndex() + 1);
        updateInput();
    }


    /**
     * When the user presses the sin(x) Button.
     */
    public void clickSin() {
        Token t = null;
        if (angleMode == DEGREE) {
            t = FunctionFactory.makeSinD();
        } else if (angleMode == RADIAN) {
            t = FunctionFactory.makeSinR();
        } else if (angleMode == GRADIAN) {
            t = FunctionFactory.makeSinG();
        }
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arcsin(x) or sin^-1(x) Button.
     */
    public void clickASin() {
        Token t = null;
        if (angleMode == DEGREE) {
            t = FunctionFactory.makeASinD();
        } else if (angleMode == RADIAN) {
            t = FunctionFactory.makeASinR();
        } else if (angleMode == GRADIAN) {
            t = FunctionFactory.makeASinG();
        }
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the cos(x) Button.
     */
    public void clickCos() {
        Token t = null;
        if (angleMode == DEGREE) {
            t = FunctionFactory.makeCosD();
        } else if (angleMode == RADIAN) {
            t = FunctionFactory.makeCosR();
        } else if (angleMode == GRADIAN) {
            t = FunctionFactory.makeCosG();
        }
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arccos(x) or cos^-1(x) Button.
     */
    public void clickACos() {
        Token t = null;
        if (angleMode == DEGREE) {
            t = FunctionFactory.makeACosD();
        } else if (angleMode == RADIAN) {
            t = FunctionFactory.makeACosR();
        } else if (angleMode == GRADIAN) {
            t = FunctionFactory.makeACosG();
        }
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the tan(x) Button.
     */
    public void clickTan() {
        Token t = null;
        if (angleMode == DEGREE) {
            t = FunctionFactory.makeTanD();
        } else if (angleMode == RADIAN) {
            t = FunctionFactory.makeTanR();
        } else if (angleMode == GRADIAN) {
            t = FunctionFactory.makeTanG();
        }
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arctan(x) or tan^-1(x) Button.
     */
    public void clickATan() {
        Token t = null;
        if (angleMode == DEGREE) {
            t = FunctionFactory.makeATanD();
        } else if (angleMode == RADIAN) {
            t = FunctionFactory.makeATanR();
        } else if (angleMode == GRADIAN) {
            t = FunctionFactory.makeATanG();
        }
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the sinh(x) Button.
     */
    public void clickSinh() {
        Token t = FunctionFactory.makeSinh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arsinh(x) Button.
     */
    public void clickASinh() {
        Token t = FunctionFactory.makeASinh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the cosh(x) Button.
     */
    public void clickCosh() {
        Token t = FunctionFactory.makeCosh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the arcosh(x) Button.
     */
    public void clickACosh() {
        Token t = FunctionFactory.makeACosh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the tanh(x) Button.
     */
    public void clickTanh() {
        Token t = FunctionFactory.makeTanh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }

    /**
     * When the user presses the artanh(x) Button.
     */
    public void clickATanh() {
        Token t = FunctionFactory.makeATanh();
        Bracket b = BracketFactory.makeOpenBracket();
        if (t != null) {
            t.addDependency(b);
            b.addDependency(t);
        }
        tokens.add(display.getRealCursorIndex(), t);
        tokens.add(display.getRealCursorIndex() + 1, b);
        display.setCursorIndex(display.getCursorIndex() + 2);
    }
}