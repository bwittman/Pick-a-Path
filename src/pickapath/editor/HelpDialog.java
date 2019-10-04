package pickapath.editor;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class HelpDialog extends JDialog {
	
	private static int SPACE = 10;
	
	public HelpDialog(Window window) {
		super(window, "Help", Dialog.ModalityType.APPLICATION_MODAL);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("General", createGeneralTab());
		tabbedPane.addTab("Prompts", createPromptsTab());
		tabbedPane.addTab("Choices", createChoicesTab());
		tabbedPane.addTab("Details", createDetailsTab());
		tabbedPane.addTab("Hotkeys", createHotkeysTab());
		add(tabbedPane);		

		// Close when the user hits escape
	  getRootPane().registerKeyboardAction(e -> {dispose();},
	      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	      JComponent.WHEN_IN_FOCUSED_WINDOW);		
		
		setSize(800, 600);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);		
	}

	private static JPanel createHotkeysTab() {		
		return createTab("To make Pick-a-Path more convenient, a number of hotkeys have been created so that many tasks can be completed with a minimum of mouse clicks. A listing of these hotkeys follows." + 
				System.lineSeparator() + System.lineSeparator() + 
				"File Hotkeys" + 
				System.lineSeparator() + System.lineSeparator() + 
				"Ctrl+N:\tCreate a new flowchart" + System.lineSeparator() +
				"Ctrl+O:\tOpen an existing flowchart" + System.lineSeparator() +
				"Ctrl+S:\tSave the current flowchart" + System.lineSeparator() +
				"Alt+F4:\tExit Pick-a-Path" + System.lineSeparator() +
				System.lineSeparator() + System.lineSeparator() +
				"Edit Hotkeys" + 
				System.lineSeparator() + System.lineSeparator() + 
				"Ctrl+Z:\tUndo last action" + System.lineSeparator() +
				"Ctrl+Y:\tRedo last action" + System.lineSeparator() +
				"Ctrl+P:\tCreate a new prompt" + System.lineSeparator() +
				"Ctrl+B:\tBegin a new choice (if a prompt is selected)" + System.lineSeparator() + 
				"Ctrl+R:\tRecolor the selected prompt" + System.lineSeparator() +
				"Ctrl+D:\tOpen the details for the selected choice" + System.lineSeparator() +
				System.lineSeparator() + System.lineSeparator() +
				"Game Hotkeys" + 
				System.lineSeparator() + System.lineSeparator() + 
				"Ctrl+G:\tPlay the current flowchart in game mode" + System.lineSeparator() +
				System.lineSeparator() + System.lineSeparator() +
				"In addition, spinning your mouse wheel will scroll either the workspace area or the Text area, depending on which one has focus. Furthermore, spinning the mouse wheel with the Ctrl key pressed will adjust the Zoom slider to zoom in or out.");
	}

	private static JPanel createGeneralTab() {
		return createTab("Pick-a-Path is a tool used to create boxes containing text, called prompts, and arrows connecting these boxes, called choices. By writing appropriate text to fill the prompts and making meaningful connections between the prompts with choices, it is possible to design a flowchart of decisions that can model almost anything: a text-based adventure game, a business workflow, a tutorial for using a tool, an interactive novel, or an educational dialogue." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Pick-a-Path initially starts in editor mode, allowing a designer to create a flowchart. The designer can also switch to game mode, in which each prompt is shown as text with the legal choices listed below. Selecting a choice moves to the next prompt in the flowchart or ends the game if a prompt has no available choices." + 
				System.lineSeparator() + System.lineSeparator() + 
				"It is also possible to launch game mode indepedently, allowing a player to walk through various choices without being able to see the full flowchart. Doing so provides a more game-like experience and prevents the player from knowing the consequences of his or her choices ahead of time." + 
				System.lineSeparator() + System.lineSeparator() + 
				"The flowchart can be saved and loaded at any time. Pick-a-Path uses a custom save file with a .pap extension. While in game mode, progress can be saved in a save file with a .ppp extension, allowing a player to return to the current point in the flowchart." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Buttons on the right side of the editor allow users to create new prompts and choices. The text that appears within these prompts and choices can be viewed and edited in the large text area at the bottom of the editor." + 
				System.lineSeparator() + System.lineSeparator() + 
				"By clicking buttons to edit the details of choices, advanced users can create choices that are only available if certain conditions are met. For example, the choice to unlock a door might only be available if the player has reached a prompt that corresponds to finding a key. The system also supports the idea of currency. Thus, it might be possible to bribe a guard if the player has accumulated a certain amount of currency through their decisions." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Prompts and choices are drawn on the dark gray workspace area and can be dragged to different locations to make the flowchart easier to understand. Moving the scrollbars at the right and bottom of this area can show prompts and choices that are outside of the currently viewable area. Another visibility tool is the zoom slider above the workspace that allows the user to zoom in and out." + 
				System.lineSeparator() + System.lineSeparator() + 
				"At the top of the editor is a text box that allows the user to give a title to the flowchart that will be displayed in game mode. Another text box allows the user to set the name of the currency. For example, \"rubles\" might be appropriate for an interactive 19th century Russian novel, but \"gold\" might fit with a fantasy adventure game.");
	}

	private static JPanel createDetailsTab() {
		return createTab("Some users may wish to take advantage of the advanced details for choices." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Choices may be configured so that they are only available under certain circumstances. Circumstances are defined in two ways, by items and by currency." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Items are used to represent global conditions. They are called \"items\" by analogy to items in role-playing or adventure games. For example, a user might gain a magic sword. With the sword, a choice to slay the dragon (instead of running away) might become available. Although it is natural to think about the items as physical objects, they need not be. An \"item\" could be \"Successful Management Review.\" When traveling through a flowchart, possessing a \"Successful Management Review\" could allow a user to take a \"Get Bonus\" choice at an appropriate prompt." + 
				System.lineSeparator() + System.lineSeparator() + 
				"The idea of conditions is a powerful one, but with great power comes great responsibility. Creating a flowchart with items can be confusing because there are many options." + 
				System.lineSeparator() + System.lineSeparator() + 
				"The first step to working with items is to select a choice and click on the Choice Details... button on the right of the editor or select the Choice Details... item from the Edit menu. In either case, the Choice Details window will appear." + 
				System.lineSeparator() + System.lineSeparator() + 
				"The left pane of this window is the Available Items pane. New items can be created and old items can be deleted here. Once an item exists, there are different options for using this item with a choice." + 
				System.lineSeparator() + System.lineSeparator() + 
				"If you want a player to gain an item when making the current choice, select the item from the Available Items pane and then click the Add button below the Items Player Gains for this Choice area. By selecting an item from the Available Items pane and clicking the Remove button in the Items Player Gains for this Choice area, you can also remove an item that you no longer wish a player to gain for making a choice." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Similarly, if you want a player to lose an item when making the current choice (perhaps because it has been consumed or is no longer useful), select the item from the Available Items pane and then click the Add button below the Items Player Loses for this Choice area. You can follow a similar process to remove the item from the list of lost items." + 
				System.lineSeparator() + System.lineSeparator() + 
				"The most useful (and most challenging) pane is the Items Player Must have to Make this Choice. This pane defines the combination of items that the player must (and must not) have in order for the selected choice be available. Unlike the gaining and losing item panes, this pane accepts arbitrary text input. The text in this pane should refer to the items by their numbers. For example, if the player needs both a Magic Sword (with item number 2) and a Magic Shield (with item number 5) in order to slay a dragon, the text in this pane should be \"2 AND 5.\" Arbitrarily complex Boolean expressions are possible. If a player needed either a Cloak of Darkness (item number 3) or a Potion of Invisibility (item number 6) to sneak by a demon but must not possess a Demon Lure (item number 10), this combination could be expressed as \"(3 OR 6) AND NOT 10.\"" + 
				System.lineSeparator() + System.lineSeparator() + 
				"Finally, currency allows a game to make certain choices available only if enough currency (in whatever units the creator has chosen) is available. The Currency Change for this Choice pane should contain a positive integer, a negative integer, or nothing. If it contains a positive integer, the player will gain that much currency by making the given choice. If it contains a negative integer, the player will lose that much currency for the given choice (and must have at least that much for the choice to be available). An empty pane or the value 0 will have no effect on the amount of currency in the user's pocket. The currency feature can be used to require a player to, for example, do a number of quests which are each rewarded with some currency (perhaps silver). When the player has accumulated enough currency through any combination of quests, he or she will be able to make a particular choice (perhaps buying a weapon or paying a toll). Naturally, the currency does not have to have a fantasy or even game-related meaning. An educational tool might require a user to accumulate a certain amount of currency (perhaps points) before a bonus question is unlocked." + 
				System.lineSeparator() + System.lineSeparator() + 
				"When you have made all the changes you want to the items and currency gained, lost, or required for a particular choice, you should click the Save and Close button. If your Items Player Must Have to Make this Choice or Currency Change for this Choice panes contain illegal text, you will not be able to save and close the window and must fix them. If, instead, you click the Cancel button, all changes made (including items added or removed to the Available Items list) will revert back to the way they were before opening the window.");
	}

	private static JPanel createChoicesTab() {
		return createTab("The other key building block of a flowchart is a choice, an arrow that joins one prompt to another. A prompt can have many choices coming out of it, signifying many options, a single choice coming out of it, signifying only a single way to move forward, or no choices at all, signifying the end of a game, story, or process." + 
				System.lineSeparator() + System.lineSeparator() + 
				"When a prompt is selected, a choice can be created by clicking on the Begin Choice... button on the right side of the editor. Then, a second prompt must be selected to link the starting prompt to the ending prompt with a choice. Alternatively, the same process can be initiated by selecting the Begin Choice... item from the Edit menu." + 
				System.lineSeparator() + System.lineSeparator() + 
				"When a choice has been created, it is shown as a line between two prompts with an arrow in the middle, indicating the direction that the choice takes between the two prompts. A selected choice is colored white, and an unselected choice is the same color as the prompt it originates from. When a choice is selected, typing in the large text area at the bottom of the editor will update the text that corresponds to the choice." + 
				System.lineSeparator() + System.lineSeparator() + 
				"A choice also has a number displayed within its arrow. This number is the choice's order in the list of choices available from a prompt. When a choice is selected, its order can be moved up or down by clicking the up and down arrows on the right." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Choices can be selected by left-clicking on them with the mouse. Also, a choice is automatically selected when it is created. Choices cannot be moved independently. Instead, they will always be drawn so that they connect the two prompts they link." + 
				System.lineSeparator() + System.lineSeparator() + 
				"If a selected choice is no longer useful, it can be deleted from the flowchart by clicking the Delete Choice button on the right.");
	}

	private static JPanel createPromptsTab() {
		return createTab("A key building block of a flowchart is the prompt, a box of text that describes a situation or decision point." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Prompts can be created either by clicking on the Make Prompt button on the right side of the editor or by selecting the Make Prompt item from the Edit menu." + 
				System.lineSeparator() + System.lineSeparator() + 
				"When a prompt has been created, it is shown as a rectangle in the dark gray workspace area. A selected prompt has a thick, white border around it. When a prompt is selected, typing in the large text area at the bottom of the editor will update the text inside of the prompt." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Prompts can be selected by left-clicking on them with the mouse. Also, a prompt is automatically selected when it is created. The selected prompt can be moved around the workspace by dragging it with the mouse. Moving a prompt down or to the right will automatically expand the workspace. There is no limit to the size of a flowchart." + 
				System.lineSeparator() + System.lineSeparator() + 
				"Prompts are randomly assigned a pastel color when they are created. This color is used only to distinguish prompts and has no deeper significance. The color can be randomly reassigned by clicking the Recolor Prompt button on the right or selecting the Recolor Prompt item from the Edit menu." + 
				System.lineSeparator() + System.lineSeparator() + 
				"If a selected prompt is no longer useful, it can be deleted from the flowchart by clicking the Delete Prompt button on the right. Warning: Deleting a prompt will also remove all of the choices that lead to or from it, but it is possible to undo a delete by selecting the Undo item from the Edit menu.");
	}
	
	private static JPanel createTab(String message) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(Editor.GAP, Editor.GAP, Editor.GAP, Editor.GAP));
		panel.setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(message);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(BorderFactory.createEmptyBorder(SPACE, SPACE, SPACE, SPACE));
		panel.add(new JScrollPane(textArea));
		return panel;
	}

}
