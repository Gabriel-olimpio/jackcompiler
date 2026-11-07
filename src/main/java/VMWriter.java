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

   public String getOutput(){
      return output.toString();
   }

}