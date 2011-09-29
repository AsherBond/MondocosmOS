

package es.igosoftware.dmvc.commands.server;

import es.igosoftware.protocol.GIntProtocolField;
import es.igosoftware.protocol.GObjectProtocolField;
import es.igosoftware.protocol.IProtocolField;


public abstract class GDRemoteEvaluationCommand
         extends
            GDAbstractServerCommand {

   private static final long                      serialVersionUID = 1L;

   private static int                             ID_COUNTER       = 0;

   protected final GIntProtocolField              _evaluationID    = new GIntProtocolField(false);
   protected final GIntProtocolField              _modelID         = new GIntProtocolField(false);
   protected final GIntProtocolField              _methodID        = new GIntProtocolField(false);
   protected final GObjectProtocolField<Object[]> _args            = new GObjectProtocolField<Object[]>();


   protected GDRemoteEvaluationCommand() {
   }


   protected GDRemoteEvaluationCommand(final int modelID,
                                       final int methodID,
                                       final Object[] args) {
      _evaluationID.set(calculateEvaluationID());

      _modelID.set(modelID);
      _methodID.set(methodID);
      _args.set(args);
   }


   private synchronized static int calculateEvaluationID() {
      return ID_COUNTER++;
   }


   @Override
   public final boolean isComposite() {
      return false;
   }


   public final int getEvaluationID() {
      return _evaluationID.get();
   }


   @Override
   protected IProtocolField[] getProtocolFields() {
      return new IProtocolField[] {
                        _evaluationID,
                        _modelID,
                        _methodID,
                        _args
      };
   }

}
