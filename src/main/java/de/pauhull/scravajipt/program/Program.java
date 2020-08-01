package de.pauhull.scravajipt.program;

import de.pauhull.scravajipt.evaluator.Evaluator;
import de.pauhull.scravajipt.instructions.Instruction;
import de.pauhull.scravajipt.instructions.InstructionContainer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Program implements InstructionContainer {

    public transient Evaluator evaluator;
    public List<Variable> variables;
    public List<Instruction> instructions;
    public transient int currentInstruction;

    public Program() {

        this.evaluator = new Evaluator(this);
        this.variables = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.currentInstruction = 0;
    }

    public void run() throws ProgramException {

        currentInstruction = 0;

        while(currentInstruction < instructions.size()) {

            Instruction instruction = instructions.get(currentInstruction);
            instruction.run(this);
            currentInstruction++;
        }
    }

    public void debug() {

        for(Variable variable : variables) {

            System.out.println(String.format("Variable \"%s\": Type %s, Value: \"%s\"", variable.name, variable.type.toString(), variable.value.toString()));
        }

    }

    public JSONObject toJson() {

        JSONArray array = new JSONArray();

        for(Instruction instruction : instructions) {
            array.put(instruction.toJson());
        }

        JSONObject object = new JSONObject();
        object.put("instructions", array);

        return object;
    }

    public Program fromJson(JSONObject object) {

        JSONArray array = object.getJSONArray("instructions");

        for(int i = 0; i < array.length(); i++) {

            JSONObject arrayObject = array.getJSONObject(i);

            try {
                Class<?> clazz = Class.forName("de.pauhull.scravajipt.instructions." + arrayObject.getString("type"));
                Instruction instruction = (Instruction) clazz.getMethod("fromJson", JSONObject.class).invoke(clazz.newInstance(), arrayObject);
                this.instructions.add(instruction);
            } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public List<Instruction> getContaining() {
        return instructions;
    }
}
