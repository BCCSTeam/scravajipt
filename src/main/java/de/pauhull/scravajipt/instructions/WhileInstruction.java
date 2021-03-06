package de.pauhull.scravajipt.instructions;

import de.pauhull.scravajipt.program.Program;
import de.pauhull.scravajipt.program.ProgramException;
import de.pauhull.scravajipt.program.Variable;
import de.pauhull.scravajipt.util.InstructionUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class WhileInstruction implements Instruction, InstructionContainer {

    public int line;
    public String condition;
    public List<Instruction> instructions;

    public WhileInstruction() {
    }

    public WhileInstruction(int line, String condition, List<Instruction> instructions) {

        this.line = line;
        this.condition = condition;
        this.instructions = instructions;
    }

    @Override
    public void run(Program program) {

        while (true) {

            Variable temp = new Variable();
            program.evaluator.evaluate(condition, temp, line);

            if (temp.type != Variable.Type.BOOL) {
                throw new ProgramException(line, "Invalid condition");
            }

            if (!((boolean) temp.value)) {
                break;
            }

            for (Instruction instruction : instructions) {
                instruction.run(program);
            }
        }
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public JSONObject toJson() {

        JSONObject object = new JSONObject();

        object.put("type", "WhileInstruction");
        object.put("line", line);
        object.put("condition", condition);
        JSONArray array = new JSONArray();
        instructions.forEach(i -> array.put(i.toJson()));
        object.put("instructions", array);

        return object;
    }

    @Override
    public Instruction fromJson(JSONObject object) {

        this.line = object.getInt("line");
        this.condition = object.getString("condition");
        this.instructions = InstructionUtil.instructionListFromJsonArray(object.getJSONArray("instructions"));

        return this;
    }

    @Override
    public List<Instruction> getContaining() {
        return instructions;
    }
}
