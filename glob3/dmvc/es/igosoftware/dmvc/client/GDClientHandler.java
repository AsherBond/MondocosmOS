/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


package es.igosoftware.dmvc.client;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.MessageEvent;

import es.igosoftware.dmvc.GDHandler;
import es.igosoftware.dmvc.commands.client.GDAddPropertyChangeListenerResultCommand;
import es.igosoftware.dmvc.commands.client.GDCompositeClientCommand;
import es.igosoftware.dmvc.commands.client.GDEvaluationAnswerCommand;
import es.igosoftware.dmvc.commands.client.IDClientCommand;
import es.igosoftware.dmvc.commands.server.GDAddPropertyChangeListenerCommand;
import es.igosoftware.dmvc.commands.server.GDRemoteSynchronousEvaluationCommand;


@ChannelPipelineCoverage("all")
public class GDClientHandler
         extends
            GDHandler {

   private final GDClient                                       _client;

   private final List<GDEvaluationAnswerCommand>                _evaluationAnswers                                 = new LinkedList<GDEvaluationAnswerCommand>();
   private final Lock                                           _evaluationAnswersLook                             = new ReentrantLock();
   private final Condition                                      _evaluationAnswersNotEmptyCondition                = _evaluationAnswersLook.newCondition();

   private final List<GDAddPropertyChangeListenerResultCommand> _addPropertyChangeListenerResults                  = new LinkedList<GDAddPropertyChangeListenerResultCommand>();
   private final Lock                                           _addPropertyChangeListenerResultsLook              = new ReentrantLock();
   private final Condition                                      _addPropertyChangeListenerResultsNotEmptyCondition = _addPropertyChangeListenerResultsLook.newCondition();


   public GDClientHandler(final GDClient client) {
      _client = client;
   }


   @Override
   public void messageReceived(final ChannelHandlerContext ctx,
                               final MessageEvent e) {
      super.messageReceived(ctx, e);

      final IDClientCommand command = (IDClientCommand) e.getMessage();

      if (command.isComposite()) {
         final GDCompositeClientCommand composite = (GDCompositeClientCommand) command;

         for (final IDClientCommand child : composite.getChildren()) {
            processCommand(e, child);
         }
      }
      else {
         processCommand(e, command);
      }
   }


   private void processCommand(final MessageEvent e,
                               final IDClientCommand command) {
      if (command.isSynchronousEvaluationAnswer()) {
         _evaluationAnswersLook.lock();
         try {
            _evaluationAnswers.add((GDEvaluationAnswerCommand) command);
            _evaluationAnswersNotEmptyCondition.signal();
         }
         finally {
            _evaluationAnswersLook.unlock();
         }
      }
      else if (command instanceof GDAddPropertyChangeListenerResultCommand) {
         _addPropertyChangeListenerResultsLook.lock();
         try {
            _addPropertyChangeListenerResults.add((GDAddPropertyChangeListenerResultCommand) command);
            _addPropertyChangeListenerResultsNotEmptyCondition.signal();
         }
         finally {
            _addPropertyChangeListenerResultsLook.unlock();
         }
      }
      else {
         command.evaluateInClient(e.getChannel(), _client);
      }
   }


   public GDEvaluationAnswerCommand waitForAnswer(final GDRemoteSynchronousEvaluationCommand command) {
      while (true) {
         _evaluationAnswersLook.lock();
         try {
            while (_evaluationAnswers.isEmpty()) {
               _evaluationAnswersNotEmptyCondition.awaitUninterruptibly();
            }
            for (final GDEvaluationAnswerCommand answer : _evaluationAnswers) {
               if (command.isYourAnswer(answer)) {
                  _evaluationAnswers.remove(answer);
                  return answer;
               }
            }
            //            _evaluationAnswersNotEmptyCondition.signal();
         }
         finally {
            _evaluationAnswersLook.unlock();
         }
      }
   }


   public GDAddPropertyChangeListenerResultCommand waitForAnswer(final GDAddPropertyChangeListenerCommand command) {
      while (true) {
         _addPropertyChangeListenerResultsLook.lock();
         try {
            while (_addPropertyChangeListenerResults.isEmpty()) {
               _addPropertyChangeListenerResultsNotEmptyCondition.awaitUninterruptibly();
            }
            for (final GDAddPropertyChangeListenerResultCommand answer : _addPropertyChangeListenerResults) {
               if (command.isYourAnswer(answer)) {
                  _addPropertyChangeListenerResults.remove(answer);
                  return answer;
               }
            }
            //            _addPropertyChangeListenerResultsNotEmptyCondition.signal();
         }
         finally {
            _addPropertyChangeListenerResultsLook.unlock();
         }
      }
   }


}
