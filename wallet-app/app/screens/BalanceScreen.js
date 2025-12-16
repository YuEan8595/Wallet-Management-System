import { useEffect, useState } from 'react';
import { Text, View } from 'react-native';
import api from '../services/api';

export default function BalanceScreen() {
  const [balance, setBalance] = useState(null);

  useEffect(() => {
    api.get('/wallets/1/balance')
      .then(console.log("Balance response:", res.data))
      .then(res => setBalance(res.data.balance))
      .catch(err => console.error(err));
  }, []);

  return (
    <View>
      <Text>Wallet Balance:</Text>
      <Text>{balance}</Text>
    </View>
  );
}