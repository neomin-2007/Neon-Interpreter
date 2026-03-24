package org.neomin.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Tokenizer {

    private final List<String> executionLines = new ArrayList<>();
    private final Map<String, Integer> labels = new HashMap<>();
    private int programCounter = 0;

    private final Map<String, Consumer<String[]>> commands = new HashMap<>();
    private final Object[] registers = new Object[1024];

    public Tokenizer() {
        commands.put("label", this::label);
        commands.put("jmp", this::jmp);

        commands.put("put", this::put);
        commands.put("copy", this::copy);
        commands.put("print", this::print);
        commands.put("clean", this::clean);

        commands.put("eq", this::eq);
        commands.put("gteq", this::gt);
        commands.put("lteq", this::lt);
        commands.put("gt", this::gt);
        commands.put("lt", this::lt);

        commands.put("inc", this::inc);
        commands.put("dec", this::dec);
        commands.put("sum", this::sum);
        commands.put("sub", this::sub);
        commands.put("mut", this::mut);
        commands.put("div", this::div);
    }

    public void start() {
        preprocess();

        programCounter = 0;

        while (programCounter < executionLines.size()) {
            String line = executionLines.get(programCounter);
            loadLine(line);

            programCounter++;
        }
    }

    public void preprocess() {

        for (int i = 0; i < executionLines.size(); i++) {

            String[] args = TokenUtils.tokenize(executionLines.get(i));

            if (args[0].equalsIgnoreCase("label")) {
                labels.put(args[1], i);
            }
        }
    }

    public void insertExecutionLine(String line) {
        if (line != null && !line.isEmpty()) {
            executionLines.add(line);
        }
    }

    public void loadLine(String line) {
        String[] args = TokenUtils.tokenize(line);
        String function = args[0].toLowerCase();

        Consumer<String[]> command = commands.get(function);

        if (command != null) {
            command.accept(args);
        }
    }

    public void label(String[] args) {}

    public void jmp(String[] args) {

        String label = args[1];

        Integer target = labels.get(label);

        if (target == null) {
            throw new IllegalArgumentException("Unknown label: " + label);
        }

        programCounter = target - 1;
    }

    public void put(String[] args) {
        int address = TokenUtils.getRegisterIndex(args[1]);

        final String value = args[2];
        if (TokenUtils.isNumeric(value)) {
            registers[address] = Integer.parseInt(value);
        } else if (TokenUtils.isFloatNumber(value)) {
            registers[address] = Float.parseFloat(value);
        } else {
            registers[address] = value;
        }
    }

    public void copy(String[] args) {
        int rAddress = TokenUtils.getRegisterIndex(args[1]);
        int cAddress = TokenUtils.getRegisterIndex(args[2]);

        final Object objectToCopy = registers[cAddress];

        if (objectToCopy != null) {
            registers[rAddress] = objectToCopy;
        }
    }

    public void print(String[] args) {
        int address = TokenUtils.getRegisterIndex(args[1]);

        final Object value = registers[address];

        if (value instanceof String) {
            System.out.println("String result: " + value);
        } else if (value instanceof Integer) {
            System.out.println("Int result: " + value);
        } else if (value instanceof Float) {
            System.out.println("Float result: " + value);
        }else if (value instanceof Boolean) {
            System.out.println("Boolean result: " + value);
        }
    }

    public void clean(String[] args) {
        int address = TokenUtils.getRegisterIndex(args[1]);
        registers[address] = null;
    }

    public void eq(String[] args) {
        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        final Object aObject = registers[aAddress];
        final Object bObject = registers[bAddress];

        if (aObject == null || bObject == null) {
            return;
        }

        registers[rAddress] = aObject.equals(bObject);
    }

    public void gt(String[] args) {
        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        final Object aObject = registers[aAddress];
        final Object bObject = registers[bAddress];

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) > ((Integer) bObject);
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) > ((Float) bObject);
        }

        throw new IllegalArgumentException("Type mismatch for GT operation");
    }

    public void lt(String[] args) {
        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        final Object aObject = registers[aAddress];
        final Object bObject = registers[bAddress];

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) < ((Integer) bObject);
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) < ((Float) bObject);
        }

        throw new IllegalArgumentException("Type mismatch for LT operation");
    }

    public void gtEq(String[] args) {
        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        final Object aObject = registers[aAddress];
        final Object bObject = registers[bAddress];

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) >= ((Integer) bObject);
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) >= ((Float) bObject);
        }

        throw new IllegalArgumentException("Type mismatch for LTEQ operation");
    }

    public void ltEq(String[] args) {
        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        final Object aObject = registers[aAddress];
        final Object bObject = registers[bAddress];

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) <= ((Integer) bObject);
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) <= ((Float) bObject);
        }

        throw new IllegalArgumentException("Type mismatch for LTEQ operation");
    }

    public void inc(String[] args) {
        int address = TokenUtils.getRegisterIndex(args[1]);

        Object addressObject = registers[address];

        if (addressObject == null) {
            return;
        }

        if (addressObject instanceof Float) {
            if (args.length > 2 && !args[2].isEmpty() && TokenUtils.isFloatNumber(args[2])) {
                addressObject = ((Float) addressObject) + Float.parseFloat(args[2]);
            } else {
                addressObject = ((Float) addressObject) + 1;
            }
        } else if (addressObject instanceof Integer) {
            if (args.length > 2 && !args[2].isEmpty() && TokenUtils.isNumeric(args[2])) {
                addressObject = ((Integer) addressObject) + Integer.parseInt(args[2]);
            } else {
                addressObject = ((Integer) addressObject) + 1;
            }
        }

        registers[address] = addressObject;
    }

    public void dec(String[] args) {
        int address = TokenUtils.getRegisterIndex(args[1]);

        Object addressObject = registers[address];

        if (addressObject == null) {
            return;
        }

        if (addressObject instanceof Float) {
            if (args.length > 2 && !args[2].isEmpty() && TokenUtils.isFloatNumber(args[2])) {
                addressObject = ((Float) addressObject) - Float.parseFloat(args[2]);
            } else {
                addressObject = ((Float) addressObject) - 1;
            }
        } else if (addressObject instanceof Integer) {
            if (args.length > 2 && !args[2].isEmpty() && TokenUtils.isNumeric(args[2])) {
                addressObject = ((Integer) addressObject) - Integer.parseInt(args[2]);
            } else {
                addressObject = ((Integer) addressObject) - 1;
            }
        }

        registers[address] = addressObject;
    }

    public void sum(String[] args) {

        int firstAddress = TokenUtils.getRegisterIndex(args[1]);
        int secondAddress = TokenUtils.getRegisterIndex(args[2]);
        int replaceAddress = TokenUtils.getRegisterIndex(args[3]);

        Object firstObject = registers[firstAddress];
        Object secondObject = registers[secondAddress];

        if (firstObject == null || secondObject == null) {
            return;
        }

        if (firstObject instanceof String && secondObject instanceof String) {
            registers[replaceAddress] = ((String) firstObject) + ((String) secondObject);
            return;
        }

        if (firstObject instanceof Integer && secondObject instanceof Integer) {
            registers[replaceAddress] = ((Integer) firstObject) + ((Integer) secondObject);
            return;
        }

        if (firstObject instanceof Float && secondObject instanceof Float) {
            registers[replaceAddress] = ((Float) firstObject) + ((Float) secondObject);
            return;
        }

        throw new IllegalArgumentException("Type mismatch for SUM operation");
    }

    public void sub(String[] args) {

        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        Object aObject = registers[aAddress];
        Object bObject = registers[bAddress];

        if (aObject == null || bObject == null) {
            return;
        }

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) - ((Integer) bObject);
            return;
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) - ((Float) bObject);
            return;
        }

        throw new IllegalArgumentException("Type mismatch for SUBTRACT operation");
    }

    public void mut(String[] args) {

        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        Object aObject = registers[aAddress];
        Object bObject = registers[bAddress];

        if (aObject == null || bObject == null) {
            return;
        }

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) * ((Integer) bObject);
            return;
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) * ((Float) bObject);
            return;
        }

        throw new IllegalArgumentException("Type mismatch for MULTIPLICATION operation");
    }

    public void div(String[] args) {

        int aAddress = TokenUtils.getRegisterIndex(args[1]);
        int bAddress = TokenUtils.getRegisterIndex(args[2]);
        int rAddress = TokenUtils.getRegisterIndex(args[3]);

        Object aObject = registers[aAddress];
        Object bObject = registers[bAddress];

        if (aObject == null || bObject == null) {
            return;
        }

        if (aObject instanceof Integer && bObject instanceof Integer) {
            registers[rAddress] = ((Integer) aObject) / ((Integer) bObject);
            return;
        }

        if (aObject instanceof Float && bObject instanceof Float) {
            registers[rAddress] = ((Float) aObject) / ((Float) bObject);
            return;
        }

        throw new IllegalArgumentException("Type mismatch for DIVISION operation");
    }
}
