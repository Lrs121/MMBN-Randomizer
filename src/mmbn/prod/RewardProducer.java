package mmbn.prod;

import mmbn.ChipLibrary;
import mmbn.types.Item;

public abstract class RewardProducer extends ItemProducer<Item> {
	public RewardProducer(ChipLibrary chipLibrary, Item.Type[] itemTypes) {
		super(chipLibrary, itemTypes);
	}

	@Override
	public String getDataName() {
		return "battle reward";
	}
}
