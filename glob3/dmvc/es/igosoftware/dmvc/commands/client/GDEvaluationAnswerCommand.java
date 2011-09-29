

package es.igosoftware.dmvc.commands.client;

import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.IProtocolField;


public abstract class GDEvaluationAnswerCommand
         extends
            GDAbstractClientCommand {

   private static final long       serialVersionUID = 1L;


   private final GIntProtocolField _evaluationID    = new GIntProtocolField(false);


   protected GDEvaluationAnswerCommand() {
   }


   protected GDEvaluationAnswerCommand(final int evaluationID) {
      _evaluationID.set(evaluationID);
   }


   public abstract Object evaluationResult() throws Exception;


   @Override
   public boolean isSynchronousEvaluationAnswer() {
      return true;
   }


   public int getEvaluationID() {
      return _evaluationID.get();
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
         _evaluationID
      };
   }

}
