/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.lobbyessentials.sidebar;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

public class SidebarTransitions {

	private List<Pair<String, String>> list;
	
	private int stayIntervals;
	private int emptyIntervals;
	
	private String colorsOne;
	private String colorsTwo;
	
	public SidebarTransitions(int stayIntervals, int emptyIntervals, String colorsOne, String colorsTwo) {
		list = Lists.newArrayList();
		this.stayIntervals = stayIntervals;
		this.emptyIntervals = emptyIntervals;
		this.colorsOne = colorsOne;
		this.colorsTwo = colorsTwo;
	}

	public void addPair(String one, String two) {
		list.add(Pair.of(one, two));
	}
	
	public Pair<List<String>, List<String>> makeLists() {
		
		List<String> listOne = Lists.newArrayList();
		List<String> listTwo = Lists.newArrayList();

		int last = list.size() - 1;
		
		for (int i = 0; i < list.size(); i++) {
			
			Pair<String, String> current = list.get(i);
			Pair<String, String> next = i == last ? list.get(0) : list.get(i + 1);
				
			int maxLengthCurrent = Math.max(current.getLeft().length(), current.getRight().length());
			int maxLengthNext = Math.max(next.getLeft().length(), next.getRight().length());
			
			listOne.addAll(repeat(colorsOne + current.getLeft(), stayIntervals));
			listTwo.addAll(repeat(colorsTwo + current.getRight(), stayIntervals));
			
			listOne.addAll(makeTransitionFrames(colorsOne, current.getLeft(), next.getLeft(), maxLengthCurrent, maxLengthNext, emptyIntervals));
			listTwo.addAll(makeTransitionFrames(colorsTwo, current.getRight(), next.getRight(), maxLengthCurrent, maxLengthNext, emptyIntervals));
		}
		
		return Pair.of(listOne, listTwo);
	}
	
	
	public static List<String> repeat(String elem, int times) {
		List<String> list = Lists.newArrayList();
		
		while (times > 0) {
			list.add(elem);
			times--;
		}
		
		return list;
	}
	
	
	private static List<String> makeTransitionFrames(String colors, String start, String end, int startLength, int endLength, int emptySteps) {
		List<String> list = Lists.newArrayList();
		
		for (int i = 0; i < startLength; i++) {
			
			if (i == 0) {
				list.add(colors + start);
			} else if (i >= start.length()) {
				list.add("");
			} else {
				list.add(colors + start.substring(i));
			}
				
		}
		
		for (int i = 0; i < emptySteps; i++) {
			list.add("");
		}
		
		for (int i = endLength - 1; i >= 0; i--) {
			
			if (i >= end.length()) {
				list.add("");
			} else {
				list.add(colors + end.substring(i, end.length()));
			}
		}
		
		return list;
	}
	
}
