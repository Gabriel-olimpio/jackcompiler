public class VMWriter{
   private StringBuilder output = new StringBuilder();


   private String segmentName(Segment segment){
       return switch(segment){
         case Segment.CONSTANT -> "constant";
         case Segment.ARGUMENT -> "argument";
         case Segment.STATIC -> "static";
         case Segment.THIS -> "this";
         case Segment.THAT -> "that";
         case Segment.POINTER -> "pointer";
         case Segment.TEMP -> "temp";
         case Segment.LOCAL -> "local";
      };
   }
   private String arithmeticName(ArithmeticCommand command){
       return switch(command){
         case ArithmeticCommand.AND -> "and";
         case ArithmeticCommand.OR -> "or";
         case ArithmeticCommand.NOT -> "not";
         case ArithmeticCommand.EQ -> "eq";
         case ArithmeticCommand.GT -> "gt";
         case ArithmeticCommand.LT -> "lt";
         case ArithmeticCommand.ADD -> "add";
         case ArithmeticCommand.SUB -> "sub";
         case ArithmeticCommand.NEG -> "neg";
      };
   }

   public void writePush(Segment segment, int index){
      if(index < 0) {
         throw new IllegalArgumentException("Index negativo");
      }

      output.append("push " + segmentName(segment) + " " + index + '\n');
   }

   public void writePop(Segment segment, int index){
      if(index < 0){
         throw new IllegalArgumentException("Index negativo");
      }

      if(segment == Segment.CONSTANT){
         throw new IllegalArgumentException("Segmento é uma CONSTANT");
      }

      output.append("pop " + segmentName(segment) + " " + index + '\n');

   }

   public void writeArithmetic(ArithmeticCommand command){
        output.append(arithmeticName(command) + '\n');
   }

    public void writeLabel(String label){
        output.append("label "+ label + '\n');
    }

   public void writeGoto(String label){
        output.append( "goto " + label + '\n');
   }

   public void writeIf(String label){
        output.append("if-goto " + label + '\n');
   }

   public void writeFunction(String name, int nLocals){
       if(nLocals < 0) {
           throw new IllegalArgumentException("Quantidade variáveis locais negativa");
       }

        output.append("function " + name +  " " + nLocals + '\n');
   }

    public void writeCall(String name, int nArgs){
       if(nArgs < 0){
           throw new IllegalArgumentException("Quantidade de argumentos negativa");
       }

        output.append("call " + name +  " " + nArgs + '\n');
    }

    public void writeReturn(){
        output.append("return" + '\n');
    }





   public String getOutput(){
      return output.toString();
   }

}