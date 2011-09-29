/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.character.CharacterController;
import imi.character.avatar.Avatar;
import imi.character.statemachine.GameState;
import imi.character.statemachine.corestates.ActionInfo;
import imi.character.statemachine.corestates.ActionState;
import imi.character.statemachine.corestates.CycleActionState;
import imi.character.statemachine.corestates.FlyState;
import imi.character.statemachine.corestates.IdleState;
import imi.character.statemachine.corestates.TurnState;
import imi.character.statemachine.corestates.WalkState;
import imi.scene.animation.AnimationListener.AnimationMessageType;
import java.util.HashMap;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;

/**
 *
 * Overload AvatarContext to add playMiscAnimation
 *
 * @author paulby
 */
public class WlAvatarContext extends imi.character.avatar.AvatarContext {
    private static final Logger LOGGER =
            Logger.getLogger(WlAvatarContext.class.getName());

    private HashMap<String, ActionInfo> actionMap = new HashMap();
    private ActionInfo currentActionInfo = null;
    private String currentAnimationName = null;

    public WlAvatarContext(Avatar avatar) {
        super(avatar);

        if (avatar.getCharacterParams().isAnimateBody())
            for(ActionInfo actionInfo : getGenericAnimations()) {
                actionMap.put(actionInfo.getAnimationName(), actionInfo);
            }
    }

    @Override
    protected CharacterController instantiateController() {
        return new WlAvatarController(getavatar());
    }
    
    
/*
    @Override
    public void notifyAnimationMessage(AnimationMessageType message, int stateID)
        {
        // DO SOMETHING WITH MESSAGE IF stateID is 0
        System.out.println("XXX message: " + message + " StateID: " + stateID);

        // switch (message)
            {
        //    case EndOfCycle:
        //        break;
        //    case PlayOnceComplete:
        //        break;
        //    case TransitionComplete:
        //        break;
        //
            }

        super.notifyAnimationMessage(message, stateID);
        }
*/

    /**
     * Return the names of the animations available to this character
     * @return
     */
    public Iterable<String> getAnimationNames() {
        return actionMap.keySet();
    }

    public void playMiscAnimation(String name) {
        if (getavatar().getCharacterParams().isAnimateBody()) {
            setMiscAnimation(name);

            // Force the trigger, note that this transition is so fast that the
            // state machine may not actually change state. Therefore in triggerAlert
            // we check for the trigger and force the state change.
            //triggerReleased(TriggerNames.MiscAction.ordinal());
            triggerPressed(TriggerNames.MiscAction.ordinal());
            triggerReleased(TriggerNames.MiscAction.ordinal());
        }
    }

    public void setMiscAnimation(String animationName) {
        currentActionInfo = actionMap.get(animationName);
        ActionState action = (ActionState) gameStates.get(CycleActionState.class);
        action.setAnimationSetBoolean(false);
        currentActionInfo.apply(action);
    }

    @Override
    public void notifyAnimationMessage(AnimationMessageType message, int stateID) {
        super.notifyAnimationMessage(message, stateID);

        GameState cur = getCurrentState();

        AvatarAnimationEvent.EventType type = null;
        String animationName = null;

        if (cur instanceof IdleState && currentAnimationName != null) 
	    {
            // transition out of the current state
            type = AvatarAnimationEvent.EventType.STOPPED;
            animationName = currentAnimationName;
            currentAnimationName = null;
            } 
	else if (cur instanceof CycleActionState) 
	    {
            switch (message) 
		{
                case TransitionComplete:
                    type = AvatarAnimationEvent.EventType.STARTED;
                    animationName = cur.getAnimationName();
                    currentAnimationName = animationName;
                    break;
                case PlayOnceComplete:
                    type = AvatarAnimationEvent.EventType.STOPPED;
                    animationName = cur.getAnimationName();
                    currentAnimationName = null;
                    break;
                }
            }
	else if (cur instanceof TurnState)
	    {
	    type = AvatarAnimationEvent.EventType.STARTED;
	    currentAnimationName = "Turn";
	    animationName = currentAnimationName;
	    }
	else if (cur instanceof WalkState)
	    {
	    type = AvatarAnimationEvent.EventType.STARTED;
	    currentAnimationName = "Walk";
	    animationName = currentAnimationName;
	    }
	else if (cur instanceof IdleState)
	    {
            type = AvatarAnimationEvent.EventType.STOPPED;
	    }

        if (type == null) 
	    {
            return;
            }

        AvatarAnimationEvent aee = new AvatarAnimationEvent(type, getavatar(),
                                                            animationName);
        ClientContext.getInputManager().postEvent(aee, getavatar());
    }

    @Override
    protected void triggerAlert(int trigger, boolean pressed) {
        if (!pressed) {
            return;
        }
        
        switch (TriggerNames.values()[trigger]) {
//            case MiscAction:
//                // Force animation to play if this is a Misc trigger
//                setCurrentState((ActionState) gameStates.get(CycleActionState.class));
//                break;
                
            case Idle:
                // force idle by interrupting the current animation
                GameState state = getCurrentState();
                if (state instanceof ActionState) {
                    state.notifyAnimationMessage(AnimationMessageType.PlayOnceComplete);
                }
                break;
        }
    }
}
